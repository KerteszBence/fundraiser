package org.fundraiser.service;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.dto.EmailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailSenderService {
    private static final String SENDER = "fundiverse.noreply@gmail.com";
    private JavaMailSender mailSender;

    @Autowired
    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(EmailData emailData) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER);
        message.setTo(emailData.getReceiver());
        message.setSubject(emailData.getSubject());
        message.setText(emailData.getMessageBody());

        mailSender.send(message);
        log.info("E-mail sent to {} with subject '{}'", emailData.getReceiver(), emailData.getSubject());
    }

}