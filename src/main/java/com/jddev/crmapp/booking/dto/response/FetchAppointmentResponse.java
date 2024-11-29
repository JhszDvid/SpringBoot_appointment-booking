package com.jddev.crmapp.booking.dto.response;

import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class FetchAppointmentResponse {

    private Long id;

    private LocalDate date;

    private AppointmentStatus status;

    private LocalTime startTime;

    private Short durationInMinutes;

    private String description;

    private Float price;

    private String booked_by;

    public FetchAppointmentResponse(Appointment appointment) {
        setId(appointment.getId());
        setDate(appointment.getDate());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Short getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Short durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getBooked_by() {
        return booked_by;
    }

    public void setBooked_by(String booked_by) {
        this.booked_by = booked_by;
    }
}
