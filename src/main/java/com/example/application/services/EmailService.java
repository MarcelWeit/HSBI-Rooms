package com.example.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Die JavaMailSender-Bibliothek wird verwendet, um E-Mail-Nachrichten zu senden
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Eine Methode zum Senden einfacher Text-E-Mails
    // Diese Methode nimmt den Empfänger, den Betreff und den Text der Nachricht als Parameter
    public void sendSimpleMessage(String receiver, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);        // Setzen des Empfängers der Nachricht
        message.setSubject(subject);    // Setzen des Betreffs der Nachricht
        message.setText(text);          // Setzen des Textinhalts der Nachricht
        mailSender.send(message);       // Senden der Nachricht über JavaMailSender
    }

    // Eine Methode zum Senden einer Willkommens-E-Mail an neue Benutzer
    public void sendWelcomeEmail(String email) {
        String subject = "Willkommen bei HSBI Rooms!";
        String text = "Vielen Dank für Ihre Registrierung bei HSBI Rooms. Ihr Konto wird überprüft und nach der Freischaltung erhalten Sie eine Benachrichtigung. Anschließend können Sie sich einloggen.";
        sendSimpleMessage(email, subject, text);
    }

    // Eine Methode zum Senden einer E-Mail-Benachrichtigung, dass ein Benutzerkonto freigeschaltet wurde
    public void sendAprovedMail(String email){
        String subject = "Account freigeschaltet";
        String text = "Ihr Account wurde freigeschaltet. Sie können sich nun bei HSBI Rooms anmelden.";
        sendSimpleMessage(email, subject, text);
    }
}

