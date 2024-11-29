package com.jddev.crmapp.booking.service;

import com.jddev.crmapp.booking.dto.request.BookAppointmentNewCustomerRequest;
import
com.jddev.crmapp.booking.dto.request.BookAppointmentRequest;
import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.model.Appointment;
import java.time.LocalDate;

import java.util.List;
public interface BookingService {
    public Appointment BookAppointment(BookAppointmentRequest body, Long appointmentID);
    public List<Appointment> FetchAvailableAppointmentsByDate(LocalDate day);


}
