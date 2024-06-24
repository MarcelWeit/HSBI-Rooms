package com.example.application.services;

import com.example.application.data.entities.PasswordResetToken;
import com.example.application.data.entities.User;
import com.example.application.repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service für das Zurücksetzen des Passwortes
 *
 * @author Gabriel Greb
 */

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRepository tokenRepository;

    // Generieren einer einzigartigen Token-ID mit UUID
    public PasswordResetToken createToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Token läuft nach 15 Minuten ab
        token.setUser(user);
        return tokenRepository.save(token);
    }

    // Methode zur Validierung eines übergebenen Tokens
    public PasswordResetToken validateToken(String token) {
        // Abrufen des Tokens aus der Datenbank
        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        // Überprüfen, ob das Token existiert
        if (resetToken == null){
            return null;
        }
        // Überprüfen, ob das Token nicht abgelaufen ist
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            deleteToken(resetToken);
            return null;
        }
        return resetToken; // Rückgabe des gültigen Tokens
    }

    // Methode zum Löschen eines Tokens aus der Datenbank
    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}

