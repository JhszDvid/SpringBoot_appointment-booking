package com.jddev.crmapp.booking.service.impl;

import com.jddev.crmapp.booking.dto.request.CreateSingleAppointment;
import com.jddev.crmapp.booking.dto.request.GenerateAppointmentRequest;
import com.jddev.crmapp.booking.dto.request.ModifyAppointmentRequest;
import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.repository.AppointmentRepository;
import com.jddev.crmapp.booking.service.AppointmentService;
import com.jddev.crmapp.customer.model.Customer;
import com.jddev.crmapp.customer.repository.CustomerRepository;
import com.jddev.crmapp.exception.*;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.event.BookingCancelEvent;
import com.jddev.crmapp.utility.event.ClientReminderSentEvent;
import com.jddev.crmapp.utility.event.StatUpdateEvent;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ApplicationEventPublisher publisher;

    private static final Map<AppointmentStatus, EnumSet<AppointmentStatus>> validTransitions = Map.of(
            AppointmentStatus.AVAILABLE, EnumSet.of(AppointmentStatus.BOOKED, AppointmentStatus.CANCELED),
            AppointmentStatus.BOOKED, EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELED),
            AppointmentStatus.COMPLETED, EnumSet.noneOf(AppointmentStatus.class),
            AppointmentStatus.CANCELED, EnumSet.noneOf(AppointmentStatus.class)
    );

    private final DbService dbService;

    public AppointmentServiceImpl(ApplicationEventPublisher publisher, DbService dbService) {
        this.publisher = publisher;
        this.dbService = dbService;
    }

    private ArrayList<LocalTime> GenerateStartTimes(LocalTime startTime, LocalTime endTime, Short duration){
        ArrayList<LocalTime> startTimes = new ArrayList<>();
        LocalTime currentTime = startTime;
        while(!currentTime.isAfter(endTime.minusMinutes(duration))) {
            startTimes.add(currentTime);
            currentTime = currentTime.plusMinutes(duration);
        }
        return startTimes;
    }


    private boolean doIntervalsOverlap(LocalTime startTime, LocalTime endTime, LocalTime secondStartTime, LocalTime secondEndTime) {
        if(startTime == null || endTime == null || secondStartTime == null || secondEndTime == null)
            return false;
        return startTime.isBefore(secondEndTime) && secondStartTime.isBefore(endTime);
    }

    @Override
    @Transactional
    public APIResponseObject GenerateAppointments(GenerateAppointmentRequest body) {
        logger.info("GenerateAppointments started, params: " + body);

        LocalDate startDate = body.startDate();
        LocalDate endDate = body.endDate().plusDays(1);

        LocalTime startTime = body.startTime();
        LocalTime endTime = body.endTime();

        ArrayList<Appointment> appointments = new ArrayList<>();

        ArrayList<LocalTime> times = GenerateStartTimes(startTime, endTime, body.duration());
        List<LocalDate> dateList = startDate.datesUntil(endDate).toList();

        // Use 2pointer method here so the runtime is slightly better
        Integer pointerDate = 0;
        Integer pointerTime = 0;
        while(pointerDate < dateList.size()){
            LocalDate date = dateList.get(pointerDate);
            // Skip the whole day if it's in the exclusion list
            if(body.excludedDays().contains(date.getDayOfWeek().getValue())) {
                logger.trace("Skipping date " + date + " (" + date.getDayOfWeek() + ")");
                pointerDate += 1;
                pointerTime = 0;
                continue;
            }
            // Also skip specific dates
            if(body.excludedDates().contains(date)){
                logger.trace("Skipping date (specified on exception list) " + date + " (" + date.getDayOfWeek() + ")");
                pointerDate += 1;
                pointerTime = 0;
                continue;
            }
            LocalTime time = times.get(pointerTime);

            // Skip breaks
            if (doIntervalsOverlap(time, time.plusMinutes(body.duration()), body.breakStart(), body.breakEnd())) {
                pointerTime += 1;
                continue;
            }

            Appointment app = new Appointment.Builder()
                    .withDate(date)
                    .withStatus(AppointmentStatus.AVAILABLE)
                    .withStartTime(time)
                    .withDuration(body.duration())
                    .build();
            // add the created appointment to the appointment list
            appointments.add(app);

            pointerTime += 1;
            if(pointerTime >= times.size()) {
                pointerDate += 1;
                pointerTime = 0;
            }
        }

        dbService.saveAll(AppointmentRepository.class, appointments);

        logger.info("end");
        return new APIResponseObject.Builder().withMessage("Appointments successfully generated").build();
    }

    @Override
    public APIResponseObject CreateSingleAppointment(CreateSingleAppointment body) {
        logger.info("CreateSingleAppointment started");
        Customer bookbyCustomer = null;
        if(body.status() == AppointmentStatus.BOOKED) {
            Customer bookingCustomer = dbService.executeCustomMethod(CustomerRepository.class,
                repo -> ((CustomerRepository) repo).findByEmail(body.bookedByEmail())
            )
            .orElseThrow(() -> new UserNotFoundException("No customer found with this email!"));
            bookbyCustomer = bookingCustomer;
        }

        Appointment newAppointment = new Appointment.Builder()
                .withDate(body.startDate())
                .withDuration(body.durationInMinutes())
                .withDescription(body.description())
                .withPrice(body.price())
                .withStartTime(body.startTime())
                .withStatus(body.status())
                .withBookedBy(bookbyCustomer)
            .build();

        dbService.save(AppointmentRepository.class, newAppointment);

        logger.info("end");
        return new APIResponseObject.Builder()
                .withMessage("Successfully created appointment")
                .withObject(newAppointment)
                .build();
    }

    @Override
    public APIResponseObject ModifyAppointment(Long appointmentID, ModifyAppointmentRequest body) {
        logger.info("ModifyAppointment started");
        Appointment appointment = dbService.findById(AppointmentRepository.class, appointmentID)
                .orElseThrow(() -> new AppointmentNotAvailableException("The appointment you're trying to modify does not exist"));;

        if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED || appointment.getAppointmentStatus() == AppointmentStatus.CANCELED){
            logger.info("Trying to modify cancelled/completed appointment, exiting flow...");
            throw new AppointmentNotAvailableException("This is a cancelled appointment which you cannot modify!");
        }
        appointment.setDescription(body.description());
        appointment.setPrice(body.price());
        appointment.setDurationInMinutes(body.durationInMinutes());


        appointment.setDate(body.startDate());
        appointment.setStartTime(body.startTime());

        logger.info("Validating state transition...");
        if(body.status() != null && body.status() != appointment.getAppointmentStatus() ) {
            ValidateStateTransition(appointment.getAppointmentStatus(), body.status());
            appointment.setAppointmentStatus(body.status());
            appointment.setBooked_by(null);
        }

        if (body.status() == AppointmentStatus.BOOKED) {
            Customer bookingCustomer = dbService.executeCustomMethod(CustomerRepository.class,
                            repo -> ((CustomerRepository) repo).findByEmail(body.bookedByEmail())
                    ).orElseThrow(() -> new UserNotFoundException("No customer was found with this email!"));
            logger.info("Modifying bookedBy property to: " + bookingCustomer.getEmail());
            appointment.setBooked_by(bookingCustomer);
        }

        logger.info("Saving modified appointment...");
        dbService.save(AppointmentRepository.class, appointment);

        logger.info("Publish stat update event");
        if(appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED || appointment.getAppointmentStatus() == AppointmentStatus.CANCELED){
            publisher.publishEvent(new StatUpdateEvent());
        }

        logger.info("ModifyAppointment finished");
        return new APIResponseObject.Builder()
                .withMessage("Appointment successfully modified!")
                .withObject(appointment)
                .build();
    }
    @Override
    public List<Appointment> loadAppointmentsByWeek(String startOfWeek) {
        logger.info("LoadAppointmentsByWeek started, startOfWeek:" + startOfWeek);

        LocalDate start = LocalDate.parse(startOfWeek, dateFormatter);
        List<Appointment> appointmentList = dbService.executeCustomMethod(AppointmentRepository.class,
                repo -> ((AppointmentRepository) repo).findByDateBetween(start, start.plusDays(6))
        );
        logger.info("LoadAppointmentsByWeek finished");
        return appointmentList;
    }

    private void ValidateStateTransition(AppointmentStatus currentStatus, AppointmentStatus newStatus) {
        if (currentStatus != null && newStatus != null) {
            EnumSet<AppointmentStatus> allowedTransitions = validTransitions.getOrDefault(currentStatus, EnumSet.noneOf(AppointmentStatus.class));
            if (!allowedTransitions.contains(newStatus)) {
                BindingResult bindingResult = new BeanPropertyBindingResult(newStatus, "status");
                bindingResult.addError(new FieldError("ModifyAppointmentRequest", "Status", "Invalid state-transition: " + currentStatus + " -> " + newStatus));
                throw new InvalidStateTransitionException(bindingResult);
            }
        }
    }

    @EventListener
    protected void AppointmentReminderACK(ClientReminderSentEvent event){
        event.appointmentToACK().setReminderSent(true);
        dbService.save(AppointmentRepository.class, event.appointmentToACK());
    }



}
