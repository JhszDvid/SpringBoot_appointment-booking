package com.jddev.crmapp.utility.email.templates.impl;

import com.jddev.crmapp.booking.model.Appointment;
import com.jddev.crmapp.utility.email.templates.EmailTemplate;
import com.jddev.crmapp.utility.event.ClientReminderSentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class ClientReminderMailTemplate implements EmailTemplate {
    private final String bodyTemplate = """
            <html><body>
            <h1>Emlékeztető</h1>
            <p>A foglalt időpontjáig kevesebb, mint 24 óra van hátra! Amennyiben le szeretné mondani időpontját, ne tegye</p>
            </body></html>
            """;
    private final String subjectTemplate = "Időpont Emlékeztető";

    private String recipient;
    private Appointment appointmentToAck;

    public ClientReminderMailTemplate(Appointment appointment){
        this.recipient = appointment.getBooked_by().getEmail();
        this.appointmentToAck = appointment;
    }

    public Appointment getAppointmentToAck() {
        return appointmentToAck;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public String getSubject() {
        return subjectTemplate;
    }

    @Override
    public String getBody() {
        return bodyTemplate;
    }

    @Override
    public void sendAck(ApplicationEventPublisher publisher) {
        publisher.publishEvent(new ClientReminderSentEvent(getAppointmentToAck()));
    }
}
