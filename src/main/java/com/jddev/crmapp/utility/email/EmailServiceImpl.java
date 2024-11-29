package com.jddev.crmapp.utility.email;

import com.jddev.crmapp.utility.email.templates.EmailTemplate;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@EnableRetry
public class EmailServiceImpl implements EmailService{

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    private final ApplicationEventPublisher publisher;

    public EmailServiceImpl(JavaMailSender javaMailSender, ApplicationEventPublisher publisher) {
        this.javaMailSender = javaMailSender;
        this.publisher = publisher;
    }

    @Override
    @Async
    @Retryable(retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public void send(EmailTemplate template) {
        logger.info("Mail sending started, recipient: " + template.getRecipient());
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(template.getRecipient());
            helper.setSubject(template.getSubject());
            helper.setText(template.getBody(), true);

            javaMailSender.send(message);
            logger.info("Mail sent successfully, calling ACK method");
            template.sendAck(publisher);
        } catch (Exception e) {
            logger.error("Mail sending failed, error: " + e.getMessage());
            throw new IllegalStateException("failed to send email");
        }
    }
}
