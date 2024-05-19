package com.example.application.data.repository;

import com.example.application.data.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUsername(String email);
}
