package com.jddev.crmapp.booking.model;

import com.jddev.crmapp.booking.enums.ConfirmationStatus;
import com.jddev.crmapp.booking.enums.ConfirmationTokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_confirmation")
public class BookingConfirmation {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    @MapsId
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_status", nullable = false)
    private ConfirmationStatus confirmationStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private ConfirmationTokenType confirmationTokenType;
    @Column(name="confirmed_at")
    private LocalDateTime confirmationDateTime;

    @Column(name="valid_until")
    private LocalDateTime validUntil;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @Column
    private String customerEmail;

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public ConfirmationTokenType getConfirmationTokenType() {
        return confirmationTokenType;
    }

    public void setConfirmationTokenType(ConfirmationTokenType confirmationTokenType) {
        this.confirmationTokenType = confirmationTokenType;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    protected BookingConfirmation(){}

    public LocalDateTime getConfirmationDateTime() {
        return confirmationDateTime;
    }

    public void setConfirmationDateTime(LocalDateTime confirmationDateTime) {
        this.confirmationDateTime = confirmationDateTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ConfirmationStatus getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(ConfirmationStatus confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public Appointment getAppointment() {
        return appointment;
    }



    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void reassignConfirmation(String email, ConfirmationTokenType type, String token){
        setConfirmationStatus(ConfirmationStatus.OPEN);
        setConfirmationTokenType(type);
        setValidUntil(LocalDateTime.now().plusMinutes(10));
        setCustomerEmail(email);
        setConfirmationDateTime(null);
        setToken(token);
        setValidUntil(LocalDateTime.now().plusMinutes(10));
    }


    public Long getId() {
        return id;
    }

    public static class Builder {
        BookingConfirmation bookingConfirmation ;

        public Builder(){
            bookingConfirmation = new BookingConfirmation();
            bookingConfirmation.setConfirmationStatus(ConfirmationStatus.OPEN);
            bookingConfirmation.setValidUntil(LocalDateTime.now().plusMinutes(10));
        }

        public Builder withAppointment(Appointment appointment) {
            bookingConfirmation.setAppointment(appointment);
            return this;
        }

        public Builder withTokenType(ConfirmationTokenType type){
            bookingConfirmation.setConfirmationTokenType(type);
            return this;
        }

        public Builder withToken(String token){
            bookingConfirmation.setToken(token);
            return this;
        }

        public Builder withCustomerEmail(String email){
            bookingConfirmation.setCustomerEmail(email);
            return this;
        }

        public BookingConfirmation build(){
            return bookingConfirmation;
        }
    }

}