package com.example.application.services;

import com.example.application.data.entities.Registrierung;
import com.example.application.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service für die Entität Registrierung
 *
 * @author Marcel Weithoener
 */

@Service
public class RegistrationService {

    private final RegistrationRepository repository;

    public RegistrationService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public void save(Registrierung registration) {
        repository.save(registration);
    }

    public void delete(Registrierung registrierung) {
        repository.delete(registrierung);
    }

    public List<Registrierung> findAllRegistrierungen() {
        return repository.findAll();
    }

    public boolean emailExists(String email) {
        return repository.existsByUsername(email.toLowerCase());
    }
}
