package com.jddev.crmapp.booking.service;

import com.jddev.crmapp.booking.enums.ConfirmationTokenType;
import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.booking.model.BookingConfirmation;

public interface ConfirmationService {
    public BookingConfirmation GenerateConfirmationToken(Appointment appointment, ConfirmationTokenType tokenType);
    public void SendConfirmation(BookingConfirmation confirmation);

    public void ValidateConfirmation(String token);
    public void CancelConfirmation(String token);
}
