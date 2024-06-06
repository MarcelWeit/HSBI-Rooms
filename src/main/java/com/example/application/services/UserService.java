package com.example.application.services;

import com.example.application.data.entities.User;
import com.example.application.data.repository.RegistrationRepository;
import com.example.application.data.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final RegistrationRepository registrierungRepository;

    public UserService(UserRepository repository, RegistrationRepository registrierungRepository) {
        this.repository = repository;
        this.registrierungRepository  = registrierungRepository;
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

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void save(User entity) {
        repository.save(entity);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
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

    public void updatePassword(User user, String newPassword) {
    }

}
