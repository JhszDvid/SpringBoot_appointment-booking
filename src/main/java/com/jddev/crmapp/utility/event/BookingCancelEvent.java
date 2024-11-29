package com.jddev.crmapp.utility.event;

import com.jddev.crmapp.booking.model.BookingConfirmation;

public record BookingCancelEvent(BookingConfirmation confirmation) {
}
