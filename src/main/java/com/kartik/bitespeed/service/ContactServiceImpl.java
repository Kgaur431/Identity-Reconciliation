package com.kartik.bitespeed.service;
import com.kartik.bitespeed.dto.ContactDto;
import com.kartik.bitespeed.dto.IdentifyRequest;
import com.kartik.bitespeed.dto.IdentifyResponse;
import com.kartik.bitespeed.exception.DatabaseIntegrityException;
import com.kartik.bitespeed.exception.InvalidInputException;
import com.kartik.bitespeed.model.Contact;
import com.kartik.bitespeed.model.LinkPrecedence;
import com.kartik.bitespeed.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    // handling the identify request.
    @Override
    @Transactional
    public IdentifyResponse identify(IdentifyRequest request){

        // first, we validate the input.
        if (request == null || (isEmpty(request.getEmail()) && isEmpty(request.getPhoneNumber()))) {
            throw new InvalidInputException("At least one of email or phoneNumber is required.");
        }


        String email = request.getEmail();
        String phoneNumber = request.getPhoneNumber();

        try {
            // we are finding  all contacts matching provided email or phoneNumber.
            List<Contact> matchedContacts = contactRepository.findByEmailOrPhoneNumber(email, phoneNumber);

            // If no match found then we create new primary contact and save it and then return it.
            if (matchedContacts == null || matchedContacts.isEmpty()) {
                Contact newContact = new Contact();
                newContact.setEmail(request.getEmail());
                newContact.setPhoneNumber(request.getPhoneNumber());
                newContact.setLinkPrecedence(LinkPrecedence.PRIMARY);
                newContact.setLinkedId(null);
                // createdAt/updatedAt handled by the  @PrePersist
                contactRepository.save(newContact);

                ContactDto contactDto = ContactDto.builder()
                        .primaryContatctId(newContact.getId())
                        .emails(newContact.getEmail() != null ?
                                List.of(newContact.getEmail()) : Collections.emptyList())
                        .phoneNumbers(newContact.getPhoneNumber() != null ?
                                List.of(newContact.getPhoneNumber()) : Collections.emptyList())
                        .secondaryContactIds(Collections.emptyList())
                        .build();

                return new IdentifyResponse(contactDto);

            }

            // Get all related contacts (traverse linked contacts)
            Set<Long> seen = new HashSet<>();
            Queue<Contact> queue = new LinkedList<>(matchedContacts);
            List<Contact> allLinkedContacts = new ArrayList<>();
            while (!queue.isEmpty()) {
                Contact contact = queue.poll();
                if (contact == null) {
                    continue;
                }
                if (seen.contains(contact.getId())) {
                    continue;
                }
                allLinkedContacts.add(contact);
                seen.add(contact.getId());

                List<Contact> linkedContacts = contactRepository.findByIdOrLinkedId(contact.getId());
                for (Contact linkedContact : linkedContacts) {
                    if (!seen.contains(linkedContact.getId())) {
                        queue.add(linkedContact);
                    }
                }
            }

            // Find the primary (oldest createdAt)
            Contact primary = allLinkedContacts.stream()
                    .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY  )
                    .min(Comparator.comparing(Contact::getCreatedAt)
                            .thenComparing(Contact::getId))
                    .orElse(!allLinkedContacts.isEmpty() ? allLinkedContacts.get(0) : null);   // If no explicit primary found, just take the first


            // if there’s ever a case where two or more “primary” contacts get merged.
            allLinkedContacts.stream()
                    .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY && !Objects.equals(c.getId(), primary.getId()))
                    .forEach(c -> {
                        c.setLinkPrecedence(LinkPrecedence.SECONDARY);
                        c.setLinkedId(primary.getId());
                        c.setUpdatedAt(LocalDateTime.now());
                        contactRepository.save(c);
                    });

            // let say if new info is present, add it as a secondary
            boolean emailKnown = isEmpty(request.getEmail()) ||
                    allLinkedContacts.stream().anyMatch(c -> request.getEmail().equalsIgnoreCase(c.getEmail()));
            boolean phoneKnown = isEmpty(request.getPhoneNumber()) ||
                    allLinkedContacts.stream().anyMatch(c -> request.getPhoneNumber().equalsIgnoreCase(c.getPhoneNumber()));
            if (!emailKnown || !phoneKnown) {
                Contact secondary = new Contact();
                secondary.setEmail(request.getEmail());
                secondary.setPhoneNumber(request.getPhoneNumber());
                secondary.setLinkPrecedence(LinkPrecedence.SECONDARY);
                secondary.setLinkedId(primary.getId());
                contactRepository.save(secondary);
                allLinkedContacts.add(secondary);
            }


            // Build unique and ordered lists for emails, phones, and secondaries
            // Collect emails and phones - primary goes first, rest (unique) afterwards
            List<String> emails = new ArrayList<>();
            if (primary.getEmail() != null && !primary.getEmail().isBlank()) {
                emails.add(primary.getEmail());
            }

            // Add other emails (avoid primary & avoid duplicates)
            allLinkedContacts.stream()
                    .filter(c -> !c.getId().equals(primary.getId()))
                    .map(Contact::getEmail)
                    .filter(e -> e != null && !e.isBlank() && !emails.contains(e))
                    .forEach(emails::add);

            List<String> phones = new ArrayList<>();
            if (primary.getPhoneNumber() != null && !primary.getPhoneNumber().isBlank()) {
                phones.add(primary.getPhoneNumber());
            }
            allLinkedContacts.stream()
                    .filter(c -> !c.getId().equals(primary.getId()))
                    .map(Contact::getPhoneNumber)
                    .filter(p -> p != null && !p.isBlank() && !phones.contains(p))
                    .forEach(phones::add);

            // Get secondary IDs
            List<Long> secondaryIds = allLinkedContacts.stream()
                    .filter(c -> !c.getId().equals(primary.getId()))
                    .filter(c -> c.getLinkPrecedence() == LinkPrecedence.SECONDARY)
                    .map(Contact::getId)
                    .collect(Collectors.toList());



            ContactDto contactDto = ContactDto.builder()
                    .primaryContatctId(primary.getId())
                    .emails(emails)
                    .phoneNumbers(phones)
                    .secondaryContactIds(secondaryIds)
                    .build();

            return new IdentifyResponse(contactDto);


        }
        catch (DataAccessException e){
            logger.error("Error processing contact request", e);
            throw new DatabaseIntegrityException("Database error while processing contact", e);
        }
        }


    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    }



