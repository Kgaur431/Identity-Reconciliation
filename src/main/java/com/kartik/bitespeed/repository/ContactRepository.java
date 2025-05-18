package com.kartik.bitespeed.repository;

import com.kartik.bitespeed.model.Contact;
import com.kartik.bitespeed.model.LinkPrecedence;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository  extends JpaRepository<Contact, Long>{

    List<Contact> findByEmailOrPhoneNumber(String email, String phoneNumber);

    List<Contact> findByEmail(String email);

    List<Contact> findByPhoneNumber(String phoneNumber);

    List<Contact> findByLinkedId(Long linkedId);

    List<Contact> findByLinkPrecedence(LinkPrecedence linkPrecedence);

    @Query("SELECT c FROM Contact c WHERE c.id = :id OR c.linkedId = :id")
    List<Contact> findByIdOrLinkedId(Long id);


}
