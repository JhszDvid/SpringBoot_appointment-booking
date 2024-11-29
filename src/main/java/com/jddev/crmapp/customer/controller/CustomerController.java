package com.jddev.crmapp.customer.controller;

import com.jddev.crmapp.customer.dto.request.CreateCustomerRequest;
import com.jddev.crmapp.customer.dto.request.UpdateCustomerRequest;
import com.jddev.crmapp.customer.dto.response.CreateCustomerResponse;
import com.jddev.crmapp.customer.dto.response.FetchCustomersResponse;
import com.jddev.crmapp.customer.dto.response.SearchResponse;
import com.jddev.crmapp.customer.model.Customer;
import com.jddev.crmapp.customer.service.CustomerService;
import com.jddev.crmapp.enums.SearchField;
import com.jddev.crmapp.exception.APIResponseObject;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    public ResponseEntity<?> CreateCustomer(@Valid @RequestBody CreateCustomerRequest body)
    {
        Customer createdCustomer = customerService.CreateCustomer(body);

        return new APIResponseObject.Builder()
                .withMessage("Customer succesfully created!")
                .withObject(new CreateCustomerResponse(createdCustomer.getEmail(), createdCustomer.getJoinDate()))
                .buildResponse();

    }

    @GetMapping("/customers")
    public ResponseEntity<?> FetchCustomers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "sortField", defaultValue = "customerID", required=false) String sortField,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    )
    {
        Page<Customer> custList = customerService.FetchCustomers(pageNo, pageSize, sortField, sortDir.toUpperCase());

        return new APIResponseObject.Builder()
                .withObject(new FetchCustomersResponse(custList))
                .buildResponse();
    }

    @GetMapping("/customers/search")
    public ResponseEntity<?> FetchCustomersSearch(
            @RequestParam(value = "field", required = true) String field,
            @RequestParam(value = "value", required = true) String searchValue
    )
    {
        try{
            SearchField searchField = SearchField.valueOf(field.toUpperCase());
            List<Customer> customerList = customerService.SearchCustomers(searchField, searchValue);
            return new APIResponseObject.Builder()
                    .withObject(new SearchResponse(customerList.size(), customerList))
                    .buildResponse();
        }
        catch (IllegalArgumentException e){
            // This is just to customize the return message...
            throw new IllegalArgumentException("Invalid ENUM: " + field + ". Valid options are: EMAIL, FIRSTNAME, LASTNAME");
        }


    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<?> UpdateCustomer(@PathVariable Integer id, @Valid @RequestBody UpdateCustomerRequest body)
    {
        Customer updatedCustomer = customerService.UpdateCustomer(id, body);
        return new APIResponseObject.Builder()
                .withMessage("Customer succesfully modified!")
                .withObject(updatedCustomer)
                .buildResponse();
    }

}
