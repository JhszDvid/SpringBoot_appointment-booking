package com.jddev.crmapp.booking.controller;

import com.jddev.crmapp.booking.dto.request.CreateSingleAppointment;
import com.jddev.crmapp.booking.dto.request.GenerateAppointmentRequest;
import com.jddev.crmapp.booking.dto.request.ModifyAppointmentRequest;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.service.AppointmentService;
import com.jddev.crmapp.exception.APIResponseObject;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments/generate")
    public ResponseEntity<?> GenerateAppointments(@Valid @RequestBody GenerateAppointmentRequest body)
    {
        APIResponseObject response = appointmentService.GenerateAppointments(body);
        return response.buildResponse();
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> GenerateSingleAppointment(@Valid @RequestBody CreateSingleAppointment body)
    {
        APIResponseObject response = appointmentService.CreateSingleAppointment(body);
        return response.buildResponse();
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<?> ModifyAppointment(@PathVariable Long id, @Valid @RequestBody ModifyAppointmentRequest body)
    {
        APIResponseObject response = appointmentService.ModifyAppointment(id, body);
        return response.buildResponse();
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> FetchAppointmentsByWeek(@RequestParam(value = "startOfWeek", required = true) String startOfWeek)
    {
        List<Appointment> appointmentList = appointmentService.loadAppointmentsByWeek(startOfWeek);
        return new APIResponseObject.Builder().withObject(appointmentList).buildResponse();
    }
}
