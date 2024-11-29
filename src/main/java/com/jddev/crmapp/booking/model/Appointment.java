package com.jddev.crmapp.booking.model;

import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.customer.model.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.time.LocalDate;


@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private AppointmentStatus status;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "duration", nullable = false)
    private Short durationInMinutes;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Float price;
    @Column(name ="reminder_sent", nullable = false)
    private Boolean reminderSent;
    @ManyToOne(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "booked_by")
    private Customer booked_by;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    protected Appointment(){}

    public static class Builder
    {
        private Appointment appointment;

        public Builder()
        {
            appointment = new Appointment();
            appointment.setPrice(0.0f);
            appointment.setReminderSent(false);

        }
        public Builder withDate(LocalDate date)
        {
            appointment.setDate(date);
            return this;
        }
        public Builder withStatus(AppointmentStatus status)
        {
            appointment.setAppointmentStatus(status);
            return this;
        }

        public Builder withBookedBy(Customer bookedBy)
        {
            if(bookedBy != null)
                appointment.setBooked_by(bookedBy);
            return this;
        }
        public Builder withStartTime(LocalTime startTime)
        {
            appointment.setStartTime(startTime);
            return this;
        }
        public Builder withDuration(Short duration)
        {
            appointment.setDurationInMinutes(duration);
            return this;
        }
        public Builder withDescription(String description)
        {
            appointment.setDescription(description);
            return this;
        }
        public Builder withPrice(Float price)
        {
            appointment.setPrice(price);
            return this;
        }
        public Appointment build(){
            return appointment;
        }
    }

    public Customer getBooked_by() {
        return booked_by;
    }

    public void setBooked_by(Customer booked_by) {
        this.booked_by = booked_by;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        if(price == null) {
            this.price = 0f;
            return;
        }
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Short durationInMinutes) {
        if(durationInMinutes == null)
            return;
        this.durationInMinutes = durationInMinutes;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if(startTime == null)
            return;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public AppointmentStatus getAppointmentStatus() {
        return status;
    }

    public void setAppointmentStatus(AppointmentStatus status) {
        this.status = status;
        if(status.equals(AppointmentStatus.BOOKED))
            this.setBookedAt(LocalDateTime.now());
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if(date == null)
            return;

        this.date = date;
    }


    public Boolean getReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
    }
}