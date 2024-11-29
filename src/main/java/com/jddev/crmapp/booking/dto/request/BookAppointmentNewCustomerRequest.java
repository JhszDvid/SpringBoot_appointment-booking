package com.jddev.crmapp.booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record BookAppointmentNewCustomerRequest(
        @Email
        String email,
        @Size(min = 3, max = 50)
        String firstName,
        @Size(min = 3, max = 50)
        String lastName,
        @Size(max=255)
        String description
) {
}
