package com.jddev.crmapp.utility.event;

import com.jddev.crmapp.booking.model.Appointment;

public record ClientReminderSentEvent(Appointment appointmentToACK) {
}
