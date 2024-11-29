package com.jddev.crmapp.booking.service;

import com.jddev.crmapp.booking.dto.request.BookAppointmentRequest;
import com.jddev.crmapp.booking.dto.request.CreateSingleAppointment;
import com.jddev.crmapp.booking.dto.request.GenerateAppointmentRequest;
import com.jddev.crmapp.booking.dto.request.ModifyAppointmentRequest;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.exception.APIResponseObject;

import java.time.LocalDate;

import java.util.List;

public interface AppointmentService {
    public APIResponseObject GenerateAppointments(GenerateAppointmentRequest body);
    public APIResponseObject CreateSingleAppointment(CreateSingleAppointment body);
    public APIResponseObject ModifyAppointment(Long appointmentID, ModifyAppointmentRequest body);

    public List<Appointment> loadAppointmentsByWeek(String startOfWeek);

}
