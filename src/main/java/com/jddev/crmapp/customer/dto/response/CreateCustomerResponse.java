package com.jddev.crmapp.customer.dto.response;

import com.jddev.crmapp.customer.model.Customer;

import java.time.LocalDateTime;

public record CreateCustomerResponse(String email, LocalDateTime joinDate) {
}
