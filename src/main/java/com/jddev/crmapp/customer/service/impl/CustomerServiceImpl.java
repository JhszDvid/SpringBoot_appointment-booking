package com.jddev.crmapp.customer.service.impl;

import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.repository.AppointmentRepository;
import com.jddev.crmapp.customer.dto.request.CreateCustomerRequest;
import com.jddev.crmapp.customer.dto.request.UpdateCustomerRequest;
import com.jddev.crmapp.customer.model.Customer;
import com.jddev.crmapp.customer.repository.CustomerRepository;
import com.jddev.crmapp.customer.service.CustomerService;
import com.jddev.crmapp.enums.SearchField;
import com.jddev.crmapp.exception.CustomerAlreadyExistsException;
import com.jddev.crmapp.exception.UserNotFoundException;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.event.StatUpdateEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final DbService dbService;
    private final ApplicationEventPublisher publisher;
    public CustomerServiceImpl(DbService dbService, ApplicationEventPublisher publisher) {
        this.dbService = dbService;
        this.publisher = publisher;
    }

    @Override
    public Customer CreateCustomer(CreateCustomerRequest body) {
        logger.info("CreateCustomer started, params: " + body);
        boolean doesExist = dbService.executeCustomMethod(CustomerRepository.class, repo -> ((CustomerRepository) repo).findByEmail(body.email())).isPresent();
        if(doesExist)
            throw new CustomerAlreadyExistsException("There is a customer associated with this email already!");

        Customer newCustomer = new Customer.Builder()
                .withEmail(body.email())
                .withFirstName(body.firstName())
                .withLastName(body.lastName())
                .build();

        dbService.save(CustomerRepository.class, newCustomer);
        publisher.publishEvent(new StatUpdateEvent());
        logger.info("CreateCustomer finished");
        return newCustomer;
    }

    @Override
    public Page<Customer> FetchCustomers(Integer pageNo, Integer pageSize, String sortField, String sortDir) {
        Page<Customer> customerList = dbService.findAllPageable(CustomerRepository.class, pageNo, pageSize,sortField,sortDir);
        return customerList;
    }

    @Override
    public Customer UpdateCustomer(Integer customerID, UpdateCustomerRequest body) {
        logger.info("UpdateCustomer started for customer: " + customerID);
        Customer customerToUpdate = dbService.findById(CustomerRepository.class, customerID).orElseThrow(() -> new UserNotFoundException("Customer with this ID can not be found!"));
        if(body.email() != null && !body.email().isBlank())
            customerToUpdate.setEmail(body.email());

        if(body.firstName() != null && !body.firstName().isBlank())
            customerToUpdate.setFirstName(body.firstName());

        if(body.lastName() != null && !body.lastName().isBlank())
            customerToUpdate.setLastName(body.lastName());

        logger.info("Saving customer... ");
        dbService.save(CustomerRepository.class, customerToUpdate);

        return customerToUpdate;
    }

    @Override
    public List<Customer> SearchCustomers(SearchField field, String searchText) {
        logger.info("SearchCustomers started");

        List<Customer> foundList = new ArrayList<>();
        switch (field){
            case EMAIL -> foundList = dbService.executeCustomMethod(CustomerRepository.class, repo -> (CustomerRepository) repo).findByEmailLike(searchText);
            case LASTNAME -> foundList = dbService.executeCustomMethod(CustomerRepository.class, repo -> (CustomerRepository) repo).findByLastNameLike(searchText);
            case FIRSTNAME -> foundList = dbService.executeCustomMethod(CustomerRepository.class, repo -> (CustomerRepository) repo).findByFirstNameLike(searchText);
            default -> throw new IllegalArgumentException("Invalid input");
        }

        logger.info("end");
        return foundList;
    }

}
