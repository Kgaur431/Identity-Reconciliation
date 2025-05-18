package com.kartik.bitespeed.controller;

import com.kartik.bitespeed.dto.IdentifyRequest;
import com.kartik.bitespeed.dto.IdentifyResponse;
import com.kartik.bitespeed.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
public class ContactController {

    private final ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<IdentifyResponse> identify(@RequestBody IdentifyRequest request) {
        return ResponseEntity.ok(contactService.identify(request));
    }
}
