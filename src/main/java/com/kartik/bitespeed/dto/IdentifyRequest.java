package com.kartik.bitespeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyRequest {

    @Schema(required = false, description = "Email address (optional)")
    private String email;

    @Schema(required = false, description = "Phone number (optional)")
    private String phoneNumber;
}
