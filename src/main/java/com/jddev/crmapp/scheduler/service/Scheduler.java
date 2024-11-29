package com.jddev.crmapp.scheduler.service;

import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.enums.ConfirmationStatus;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.model.BookingConfirmation;
import com.jddev.crmapp.booking.repository.AppointmentRepository;
import com.jddev.crmapp.booking.repository.ConfirmationRepository;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.email.EmailService;
import com.jddev.crmapp.utility.email.templates.impl.ClientReminderMailTemplate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Component
public class Scheduler {

    Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private final DbService dbService;
    private final EmailService emailService;

    public Scheduler(DbService dbService, EmailService emailService) {
        this.dbService = dbService;
        this.emailService = emailService;
    }
    // Cron expression: second, minute, hour, day of month, month, day(s) of week
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    protected void DeleteInvalidConfirmations()
    {
        logger.info("Scheduled task DeleteInvalidConfirmations started");
        List<BookingConfirmation> invalidConfirmations = dbService.executeCustomMethod(ConfirmationRepository.class,
                (repo) -> ((ConfirmationRepository)repo).findByConfirmationStatusAndValidUntilBefore(ConfirmationStatus.OPEN, LocalDateTime.now())
        );

        List<Appointment> appointmentsToReset = new ArrayList<>();
        invalidConfirmations.forEach((confirmation) -> {
            Appointment appointmentToReset = confirmation.getAppointment();

            appointmentToReset.setAppointmentStatus(AppointmentStatus.AVAILABLE);
            appointmentToReset.setBooked_by(null);
            appointmentsToReset.add(appointmentToReset);

            confirmation.setConfirmationStatus(ConfirmationStatus.DENIED);

        });

        dbService.saveAll(AppointmentRepository.class, appointmentsToReset);
        dbService.saveAll(ConfirmationRepository.class, invalidConfirmations);

        logger.info("Freed up " + appointmentsToReset.size() + " appointments");
        logger.info("Scheduled task DeleteInvalidConfirmations finished");
    }

    // Cron expression: second, minute, hour, day of month, month, day(s) of week
    @Scheduled(cron = "0 */10 * * * *")
    protected void SendReminderMails()
    {
        logger.info("Scheduled task SendReminderMails started");
        List<Appointment> bookedAppointments = dbService.executeCustomMethod(AppointmentRepository.class,
                repo -> ((AppointmentRepository) repo).findByStatus(AppointmentStatus.BOOKED)
        );

        LocalDateTime currentDateTime = LocalDateTime.now();

        bookedAppointments.forEach((appointment) -> {
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
            long hoursUntilAppointment = Duration.between(currentDateTime, appointmentDateTime).toHours();
            long hoursPassedSinceBooking = Duration.between(appointment.getBookedAt(), currentDateTime).toHours();
            // todo: remove magic numbers
            if (hoursUntilAppointment <= 24 && hoursUntilAppointment >= 0 && !appointment.getReminderSent() && hoursPassedSinceBooking > 6) {
                logger.info("Sending reminder mail for Appointment ID: " + appointment.getId());
                emailService.send(new ClientReminderMailTemplate(appointment));
            }
        });

        logger.info("SendReminderMails finished");
    }
}
