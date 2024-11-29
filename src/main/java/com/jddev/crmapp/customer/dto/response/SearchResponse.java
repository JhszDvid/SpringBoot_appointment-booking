package com.jddev.crmapp.customer.dto.response;

import com.jddev.crmapp.customer.model.Customer;

import java.util.List;

public record SearchResponse(
        Integer total,
        List<Customer> customerList
) {
}
