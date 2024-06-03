package com.example.application.services;

import com.example.application.data.entities.PasswordResetToken;
import com.example.application.data.entities.User;
import com.example.application.repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRepository tokenRepository;

    public PasswordResetToken createToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Token läuft nach 15 Minuten ab
        token.setUser(user);
        return tokenRepository.save(token);
    }

    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            if (resetToken != null) {
                deleteToken(resetToken);
            }
            return null; // Token ist ungültig oder abgelaufen
        }
        return resetToken;
    }

    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}

