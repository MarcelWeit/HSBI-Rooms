package com.example.application.services;

import com.example.application.data.entities.PasswordResetToken;
import com.example.application.data.entities.User;
import com.example.application.data.enums.Anrede;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Role;
import com.example.application.repository.PasswordResetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetRepository tokenRepository;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void createToken() {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        token.setUser(user);

        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        PasswordResetToken createdToken = passwordResetService.createToken(user);

        assertThat(createdToken).isNotNull();
        assertThat(createdToken.getToken()).isNotNull();
        assertThat(createdToken.getExpiryDate()).isAfter(LocalDateTime.now());
        assertThat(createdToken.getUser()).isEqualTo(user);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken capturedToken = tokenCaptor.getValue();
        assertThat(capturedToken.getUser()).isEqualTo(user);
    }

    @Test
    void validateToken_validToken() {
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenString);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        when(tokenRepository.findByToken(tokenString)).thenReturn(token);
        PasswordResetToken validatedToken = passwordResetService.validateToken(tokenString);
        assertThat(validatedToken).isNotNull();
        assertThat(validatedToken.getToken()).isEqualTo(tokenString);
        verify(tokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    void validateToken_expiredToken() {
        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenString);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken(tokenString)).thenReturn(token);
        PasswordResetToken validatedToken = passwordResetService.validateToken(tokenString);
        assertThat(validatedToken).isNull();
        verify(tokenRepository).delete(token);
    }

    @Test
    void validateToken_nonexistentToken() {
        String tokenString = UUID.randomUUID().toString();
        when(tokenRepository.findByToken(tokenString)).thenReturn(null);
        PasswordResetToken validatedToken = passwordResetService.validateToken(tokenString);
        assertThat(validatedToken).isNull();
        verify(tokenRepository, never()).delete(any(PasswordResetToken.class));
    }

    @Test
    void deleteToken() {
        User user = new User(
                "testuser@example.com",
                "Mustermann",
                "Max",
                "hashedpassword",
                Set.of(Role.DOZENT),
                Fachbereich.WIRTSCHAFT,
                Anrede.HERR,
                "Dr."
        );
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        token.setUser(user);
        passwordResetService.createToken(user);
        passwordResetService.deleteToken(token);
        assertThat(passwordResetService.validateToken(token.getToken())).isNull();
    }
}