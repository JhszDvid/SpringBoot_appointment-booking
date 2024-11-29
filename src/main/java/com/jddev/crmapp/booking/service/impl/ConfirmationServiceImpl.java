package com.jddev.crmapp.booking.service.impl;

import com.jddev.crmapp.booking.enums.ConfirmationStatus;
import com.jddev.crmapp.booking.enums.ConfirmationTokenType;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.model.BookingConfirmation;
import com.jddev.crmapp.booking.repository.ConfirmationRepository;
import com.jddev.crmapp.booking.service.ConfirmationService;
import com.jddev.crmapp.exception.InternalErrorException;
import com.jddev.crmapp.exception.InvalidConfirmationException;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.email.EmailService;
import com.jddev.crmapp.utility.email.templates.impl.ClientBookingMailTemplate;

import java.time.LocalDateTime;

import com.jddev.crmapp.utility.event.BookingCancelEvent;
import com.jddev.crmapp.utility.event.BookingConfirmationEvent;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Optional;
@Service
public class ConfirmationServiceImpl implements ConfirmationService {

    private final Logger logger = LoggerFactory.getLogger(ConfirmationRepository.class);

    @Value("${url.frontend.confirmation.confirm}")
    private String confirmationURL;

    private final DbService dbService;
    private final EmailService emailService;
    private final ApplicationEventPublisher publisher;


    public ConfirmationServiceImpl(DbService dbService, EmailService emailService, ApplicationEventPublisher publisher) {
        this.dbService = dbService;
        this.emailService = emailService;
        this.publisher = publisher;
    }

    @Override
    public BookingConfirmation GenerateConfirmationToken(Appointment appointment, ConfirmationTokenType tokenType) {

        BookingConfirmation confirmation;
        Optional<BookingConfirmation> bookingConfirmationOptional = dbService.findById(ConfirmationRepository.class,appointment.getId());

        String confirmationToken = String.valueOf(appointment.getId()) +
                appointment.getBooked_by().getId() +
                UUID.randomUUID();


        if(bookingConfirmationOptional.isPresent()) {
            if(bookingConfirmationOptional.get().getConfirmationStatus() != ConfirmationStatus.DENIED){
                logger.error("Illegal state of confirmation status!");
                throw new InternalErrorException("Unexpected error!");
            }

            confirmation = bookingConfirmationOptional.get();
            confirmation.reassignConfirmation(appointment.getBooked_by().getEmail(), tokenType, confirmationToken);
        }
        else{
            confirmation = new BookingConfirmation.Builder()
                    .withAppointment(appointment)
                    .withToken(confirmationToken)
                    .withTokenType(tokenType)
                    .withCustomerEmail(appointment.getBooked_by().getEmail())
                    .build();
        }

        dbService.save(ConfirmationRepository.class, confirmation);

        return confirmation;
    }

    @Override
    public void SendConfirmation(BookingConfirmation confirmation) {
        String requestLink = confirmationURL + "/" + confirmation.getToken();
        emailService.send(new ClientBookingMailTemplate(confirmation.getCustomerEmail(), requestLink));
    }

    @Override
    @Transactional
    public void ValidateConfirmation(String token) {
        BookingConfirmation confirmation = dbService.executeCustomMethod(ConfirmationRepository.class,
                (repo) -> ((ConfirmationRepository)repo).findByToken(token)
        ).orElseThrow(() -> new InvalidConfirmationException("Invalid token"));

        if(confirmation.getValidUntil().isBefore(LocalDateTime.now()) || confirmation.getConfirmationStatus() != ConfirmationStatus.OPEN) {
            throw new InvalidConfirmationException("Token has already been confirmed or expired");
        }

        confirmation.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
        confirmation.setConfirmationDateTime(LocalDateTime.now());
        dbService.save(ConfirmationRepository.class, confirmation);
        publisher.publishEvent(new BookingConfirmationEvent(confirmation));
    }

    @Override
    @Transactional
    public void CancelConfirmation(String token) {
        BookingConfirmation confirmation = dbService.executeCustomMethod(ConfirmationRepository.class,
                (repo) -> ((ConfirmationRepository)repo).findByToken(token)
        ).orElseThrow(() -> new InvalidConfirmationException("Invalid token"));

        publisher.publishEvent(new BookingCancelEvent(confirmation));
        dbService.delete(ConfirmationRepository.class, confirmation.getId());

    }


}
