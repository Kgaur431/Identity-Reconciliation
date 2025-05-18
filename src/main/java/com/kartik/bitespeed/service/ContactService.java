package com.kartik.bitespeed.service;

import com.kartik.bitespeed.dto.IdentifyRequest;
import com.kartik.bitespeed.dto.IdentifyResponse;

public interface ContactService {
    IdentifyResponse identify(IdentifyRequest request);
}
