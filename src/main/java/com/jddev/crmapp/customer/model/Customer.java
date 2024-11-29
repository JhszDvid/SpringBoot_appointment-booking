package com.jddev.crmapp.customer.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false)
    @JsonAlias("customerID")
    private Integer customerID;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    protected Customer(){}

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return customerID;
    }

    public static class Builder
    {
        Customer newCustomer;
        public Builder(){
            newCustomer = new Customer();
        }

        public Builder withEmail(String email)
        {
            newCustomer.setEmail(email);
            return this;
        }

        public Builder withFirstName(String firstName)
        {
            newCustomer.setFirstName(firstName);
            return this;
        }

        public Builder withLastName(String lastName)
        {
            newCustomer.setLastName(lastName);
            return this;
        }
        public Customer build()
        {
            newCustomer.setJoinDate(LocalDateTime.now());
            return newCustomer;
        }
    }



}