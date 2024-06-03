package com.example.application.services;

import com.example.application.data.entities.Registrierung;
import com.example.application.data.entities.User;
import com.example.application.repository.RegistrationRepository;
import com.example.application.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository repository;
    private final RegistrationRepository registrierungRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, RegistrationRepository registrierungRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.registrierungRepository = registrierungRepository;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void updatePassword(User user, String newPassword) {
        user.setHashedPassword(passwordEncoder.encode(newPassword));
        update(user);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void save(User entity) {
        repository.save(entity);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public boolean emailExists(String email) {
        return repository.existsByUsername(email);
    }


    private boolean usernameExists(String username, Long id) {
        User user = repository.findByUsername(username);
        return user != null && !user.getId().equals(id);
    }
    //approval


    // Methods for Registrierung entity

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
        //user.setLocked(false);

        // Save the new user and delete the registration
        repository.save(user);
        registrierungRepository.delete(registrierung);
    }

}
