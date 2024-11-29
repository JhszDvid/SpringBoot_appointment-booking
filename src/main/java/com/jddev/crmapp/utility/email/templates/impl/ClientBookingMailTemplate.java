package com.jddev.crmapp.utility.email.templates.impl;

import com.jddev.crmapp.utility.email.templates.EmailTemplate;
import org.springframework.context.ApplicationEventPublisher;

public class ClientBookingMailTemplate implements EmailTemplate {
    private final String bodyTemplate = """
            <html><body>
            <h1>Kérjük erősítsd meg a foglalásod!</h1>
            <p>Az alábbi linkre kattintva megerősítheted a foglalásod:</p>
            <div>{{link}}</div>
            </body></html>
            """;
    private final String subjectTemplate = "Foglalás - Megerősítés Szükséges!";

    private String recipient;
    private String link;
    public ClientBookingMailTemplate(String recipient, String link){
        this.recipient = recipient;
        this.link = link;
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
        return bodyTemplate.replace("{{link}}", link);
    }

    @Override
    public void sendAck(ApplicationEventPublisher publisher) {
        // nothing to do here
    }
}
