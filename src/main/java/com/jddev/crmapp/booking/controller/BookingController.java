package com.jddev.crmapp.booking.controller;

import com.jddev.crmapp.booking.dto.request.BookAppointmentNewCustomerRequest;
import com.jddev.crmapp.booking.dto.request.BookAppointmentRequest;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.service.BookingService;
import com.jddev.crmapp.booking.service.ConfirmationService;
import com.jddev.crmapp.exception.APIResponseObject;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final ConfirmationService confirmationService;

    public BookingController(BookingService bookingService, ConfirmationService confirmationService) {
        this.bookingService = bookingService;
        this.confirmationService = confirmationService;
    }

    @PostMapping("/booking/{id}")
    public ResponseEntity<?> BookAppointment(@Valid @RequestBody BookAppointmentRequest body, @PathVariable Long id)
    {
        bookingService.BookAppointment(body, id);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }


    @GetMapping("/booking")
    public ResponseEntity<?> FetchAppointmentsByDate(
            @RequestParam(value = "date", required = true)
            @Valid
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
            LocalDate date
    ){
        List<Appointment> appointmentList = bookingService.FetchAvailableAppointmentsByDate(date);
        return new APIResponseObject.Builder()
                .withObject(appointmentList)
                .buildResponse();
    }

    @PostMapping("/booking/confirmation/book/{token}")
    public ResponseEntity<?> ConfirmBooking(@PathVariable String token){
        confirmationService.ValidateConfirmation(token);
        return new APIResponseObject.Builder().withMessage("Confirmation successful!").buildResponse();
    }

    @PostMapping("/booking/confirmation/cancel/{token}")
    public ResponseEntity<?> CancelBooking(@PathVariable String token){
        confirmationService.CancelConfirmation(token);
        return new APIResponseObject.Builder().withMessage("Cancellation successful!").buildResponse();
    }


}
