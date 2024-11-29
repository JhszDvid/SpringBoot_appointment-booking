package com.jddev.crmapp.customer.service;

import com.jddev.crmapp.customer.dto.request.CreateCustomerRequest;
import com.jddev.crmapp.customer.dto.request.UpdateCustomerRequest;
import com.jddev.crmapp.customer.model.Customer;
import com.jddev.crmapp.enums.SearchField;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    public Customer CreateCustomer(CreateCustomerRequest body);

    public Page<Customer> FetchCustomers(Integer pageNo, Integer pageSize, String sortField, String sortDir);

    public Customer UpdateCustomer(Integer customerID, UpdateCustomerRequest body);

    public List<Customer> SearchCustomers(SearchField field, String searchText);

}
