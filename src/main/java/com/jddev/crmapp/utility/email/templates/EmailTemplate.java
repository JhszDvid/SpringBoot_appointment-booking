package com.jddev.crmapp.utility.email.templates;

import org.springframework.context.ApplicationEventPublisher;

public interface EmailTemplate {
    public String getRecipient();
    public String getSubject();
    public String getBody();

    public void sendAck(ApplicationEventPublisher publisher);
}
