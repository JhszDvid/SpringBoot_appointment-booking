package com.jddev.crmapp.booking.service.impl;

import com.jddev.crmapp.booking.dto.request.BookAppointmentNewCustomerRequest;
import com.jddev.crmapp.booking.dto.request.BookAppointmentRequest;
import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.enums.ConfirmationTokenType;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.model.BookingConfirmation;
import com.jddev.crmapp.booking.repository.AppointmentRepository;
import com.jddev.crmapp.booking.repository.ConfirmationRepository;
import com.jddev.crmapp.booking.service.BookingService;
import com.jddev.crmapp.booking.service.ConfirmationService;
import com.jddev.crmapp.customer.dto.request.CreateCustomerRequest;
import com.jddev.crmapp.customer.model.Customer;
import com.jddev.crmapp.customer.repository.CustomerRepository;
import com.jddev.crmapp.customer.service.CustomerService;
import com.jddev.crmapp.exception.AppointmentNotAvailableException;
import com.jddev.crmapp.exception.UserNotFoundException;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.event.BookingCancelEvent;
import com.jddev.crmapp.utility.event.BookingConfirmationEvent;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final DbService dbService;
    private final ConfirmationService confirmationService;
    private final CustomerService customerService;
    public BookingServiceImpl(DbService dbService, ConfirmationService confirmationService, CustomerService customerService) {

        this.dbService = dbService;
        this.confirmationService = confirmationService;
        this.customerService = customerService;
    }

   @Override
   @Transactional
    public Appointment BookAppointment(BookAppointmentRequest body, Long appointmentID) {
        logger.info("BookAppointment started, params: " + body + " AppointmentID: " + appointmentID);
        Appointment appointmentToUpdate = dbService.findById(AppointmentRepository.class, appointmentID).orElseThrow(() -> new AppointmentNotAvailableException("Ezzel az azonosítóval időpont nem létezik!"));

        if (appointmentToUpdate.getAppointmentStatus() != AppointmentStatus.AVAILABLE){
           logger.info("Appointment unavailable");
           throw new AppointmentNotAvailableException("This appointment is not available anymore!");
        }

        if(appointmentToUpdate.getDate().isBefore(LocalDate.now()) || appointmentToUpdate.getDate().isEqual(LocalDate.now()) && appointmentToUpdate.getStartTime().isBefore(LocalTime.now().minusMinutes(30))) {
           logger.info("Appointment is in the past");
           throw new AppointmentNotAvailableException("The appointment you're trying to book is starting in less than 30 minutes!");
        }

        Customer customer;
        ConfirmationTokenType tokenType;

        if(body.customerRequest() == null) {
            customer = dbService.executeCustomMethod(CustomerRepository.class, repo -> ((CustomerRepository) repo).findByEmail(body.email()))
                    .orElseThrow(() -> new UserNotFoundException("No customer found with this email!"));
            tokenType = ConfirmationTokenType.BOOKING;
        }
        else{
            customer = customerService.CreateCustomer(body.customerRequest());
            tokenType = ConfirmationTokenType.BOTH;
        }


        appointmentToUpdate.setDescription(body.description());
        appointmentToUpdate.setAppointmentStatus(AppointmentStatus.CONFIRMATION_NEEDED);
        appointmentToUpdate.setBooked_by(customer);
        dbService.save(AppointmentRepository.class, appointmentToUpdate);

        logger.info("Appointment update successful, creating confirmation");
        BookingConfirmation confirmation = confirmationService.GenerateConfirmationToken(appointmentToUpdate, tokenType);

        // Flush the db changes before calling async task...
        dbService.flush(AppointmentRepository.class);
        dbService.flush(ConfirmationRepository.class);

        // Send notification
        confirmationService.SendConfirmation(confirmation);

        logger.info("BookAppointment finished");

        return appointmentToUpdate;
    }

    @Override
    public List<Appointment> FetchAvailableAppointmentsByDate(LocalDate day) {
        List<Appointment> appointments = dbService.executeCustomMethod(AppointmentRepository.class,
                repo -> ((AppointmentRepository) repo).findByDateAndStatus(day, AppointmentStatus.AVAILABLE)
        );

        return appointments;
    }



    @EventListener
    protected void BookingConfirmListener(BookingConfirmationEvent event){
        logger.info("AppointmentConfirmationListener started, event: " + event);
        Appointment appointment = event.confirmation().getAppointment();
        appointment.setAppointmentStatus(AppointmentStatus.BOOKED);
        dbService.save(AppointmentRepository.class, appointment);
        logger.info("AppointmentConfirmationListener finished");
    }

    @EventListener
    protected void BookingCancelListener(BookingCancelEvent event)
    {
        logger.info("BookingCancelListener started, event: " + event);
        Appointment cancelledAppointment = event.confirmation().getAppointment();
        cancelledAppointment.setAppointmentStatus(AppointmentStatus.AVAILABLE);
        cancelledAppointment.setBooked_by(null);
        dbService.save(AppointmentRepository.class, cancelledAppointment);
        logger.info("BookingCancelListener finished");
    }




}
