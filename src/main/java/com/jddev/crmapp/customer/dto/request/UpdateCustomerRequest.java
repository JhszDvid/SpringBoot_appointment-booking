package com.jddev.crmapp.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(
        @Email
        String email,
        @Size(min = 3, max = 50)
        String firstName,
        @Size(min = 3, max = 50)
        String lastName
) {
}
