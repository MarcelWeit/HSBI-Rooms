package com.example.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendSimpleMessage(String receiver, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Async
    public void sendWelcomeEmail(String email) {
        String subject = "Willkommen bei HSBI Rooms!";
        String text = "Vielen Dank für Ihre Registrierung bei HSBI Rooms. Ihr Konto wird überprüft und nach der Freischaltung erhalten Sie eine Benachrichtigung. Anschließend können Sie sich einloggen.";
        sendSimpleMessage(email, subject, text);
    }

    @Async
    public void sendAprovedMail(String email) {
        String subject = "Account freigeschaltet";
        String text = "Ihr Account wurde freigeschaltet. Sie können sich nun bei HSBI Rooms anmelden.";
        sendSimpleMessage(email, subject, text);
    }
}

