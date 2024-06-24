package com.example.application.repository;

import com.example.application.data.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository für die Entität PasswortResetToken
 *
 * @author Gabriel Greb
 */

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}