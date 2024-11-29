package com.jddev.crmapp.customer.dto.response;

import com.jddev.crmapp.customer.model.Customer;
import org.springframework.data.domain.Page;

import java.util.List;

public class FetchCustomersResponse {
    private Long numberOfRecords;
    private Integer numberOfPages;
    private Integer pageNumber;
    private Integer pageSize;
    private List<Customer> customerList;

    public FetchCustomersResponse(Page<Customer> page)
    {
        setNumberOfRecords(page.getTotalElements());
        setNumberOfPages(page.getTotalPages());
        setPageNumber(page.getNumber());
        setPageSize(page.getSize());
        setCustomerList(page.getContent());
    }

    public void setNumberOfRecords(Long numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public Long getNumberOfRecords() {
        return numberOfRecords;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }
}
