package com.jddev.crmapp.booking.dto.request;

import com.jddev.crmapp.customer.dto.request.CreateCustomerRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record BookAppointmentRequest(

        CreateCustomerRequest customerRequest,
        @Email
        String email,
        @Size(max=255)
        String description
) {
}
