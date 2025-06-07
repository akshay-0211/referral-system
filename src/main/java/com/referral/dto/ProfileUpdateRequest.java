package com.referral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;
}