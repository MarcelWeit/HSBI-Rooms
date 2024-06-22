package com.example.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendSimpleMessage() {
        String receiver = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        emailService.sendSimpleMessage(receiver, subject, text);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly(receiver);
        assertThat(capturedMessage.getSubject()).isEqualTo(subject);
        assertThat(capturedMessage.getText()).isEqualTo(text);
    }

    @Test
    void sendWelcomeEmail() {
        String email = "welcome@example.com";
        String expectedSubject = "Willkommen bei HSBI Rooms!";
        String expectedText = "Vielen Dank für Ihre Registrierung bei HSBI Rooms. Ihr Konto wird überprüft und nach der Freischaltung erhalten Sie eine Benachrichtigung. Anschließend können Sie sich einloggen.";

        emailService.sendWelcomeEmail(email);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly(email);
        assertThat(capturedMessage.getSubject()).isEqualTo(expectedSubject);
        assertThat(capturedMessage.getText()).isEqualTo(expectedText);
    }

    @Test
    void sendAprovedMail() {
        String email = "approved@example.com";
        String expectedSubject = "Account freigeschaltet";
        String expectedText = "Ihr Account wurde freigeschaltet. Sie können sich nun bei HSBI Rooms anmelden.";

        emailService.sendAprovedMail(email);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly(email);
        assertThat(capturedMessage.getSubject()).isEqualTo(expectedSubject);
        assertThat(capturedMessage.getText()).isEqualTo(expectedText);
    }
}

