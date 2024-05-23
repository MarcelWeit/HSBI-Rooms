package com.example.application.data.repository;

import com.example.application.data.entities.Registrierung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registrierung, Long> {

    boolean existsByUsername(String email);
}
