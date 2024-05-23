package com.example.application.services;

import com.example.application.data.entities.Registrierung;
import com.example.application.data.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final RegistrationRepository repository;

    public RegistrationService(RegistrationRepository repository) {
        this.repository = repository;
    }


    public void save(Registrierung registration) {
        repository.save(registration);
    }

    public boolean emailExists(String email) {
        return repository.existsByUsername(email);
    }
}
