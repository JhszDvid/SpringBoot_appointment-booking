package com.jddev.crmapp.customer.repository;

import com.jddev.crmapp.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(@NonNull String email);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.joinDate BETWEEN :startDate AND :endDate")
    Long countByDateBetween(@NonNull @Param("startDate") LocalDateTime startDateTime, @NonNull @Param("endDate") LocalDateTime endDateTime);

    @Query("select c from Customer c where c.email like %?1%")
    List<Customer> findByEmailLike(@NonNull String email);

    @Query("select c from Customer c where c.firstName like %?1%")
    List<Customer> findByFirstNameLike(@NonNull String firstName);

    @Query("select c from Customer c where c.lastName like %?1%")
    List<Customer> findByLastNameLike(@NonNull String lastName);



}
