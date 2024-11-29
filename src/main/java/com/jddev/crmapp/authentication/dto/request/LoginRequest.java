package com.jddev.crmapp.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 5, max = 150)
        String password
) {
}
