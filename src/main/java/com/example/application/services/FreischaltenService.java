package com.example.application.services;

import com.example.application.data.entities.Registrierung;
import com.example.application.data.entities.User;
import com.example.application.repository.RegistrationRepository;
import com.example.application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FreischaltenService {

    private final UserRepository userRepository;
    private final RegistrationRepository registrierungRepository;

    public FreischaltenService(RegistrationRepository registrierungRepository, UserRepository userRepository, UserRepository userRepository1) {
        this.registrierungRepository  = registrierungRepository;
        this.userRepository = userRepository;
    }

    public List<Registrierung> findAllRegistrierungen() {
        return registrierungRepository.findAll();
    }

    public void save(Registrierung registrierung) {
        registrierungRepository.save(registrierung);
    }

    public void delete(Registrierung registrierung) {
        registrierungRepository.delete(registrierung);
    }
    public void approveRegistration(Registrierung registrierung) {
        User user = new User();
        user.setUsername(registrierung.getUsername());
        user.setFirstName(registrierung.getFirstName());
        user.setLastName(registrierung.getLastName());
        user.setHashedPassword(registrierung.getHashedPassword());
        user.setRoles(Set.of(registrierung.getRole()));
        user.setFachbereich(registrierung.getFachbereich());


        //User wird aus der Registrierungstabelle gelöscht
        userRepository.save(user);
        registrierungRepository.delete(registrierung);
    }
}
