package com.jddev.crmapp.utility.email;

import com.jddev.crmapp.utility.email.templates.EmailTemplate;

public interface EmailService {
    void send(EmailTemplate template);

}
