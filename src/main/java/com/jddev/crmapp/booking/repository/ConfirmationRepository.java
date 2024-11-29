package com.jddev.crmapp.booking.repository;

import com.jddev.crmapp.booking.enums.ConfirmationStatus;
import com.jddev.crmapp.booking.model.BookingConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ConfirmationRepository extends JpaRepository<BookingConfirmation, Long> {
    Optional<BookingConfirmation> findByToken(@NonNull String token);

    List<BookingConfirmation> findByConfirmationStatus(ConfirmationStatus confirmationStatus);

    List<BookingConfirmation> findByConfirmationStatusAndValidUntilBefore(ConfirmationStatus confirmationStatus, LocalDateTime validUntil);



}
