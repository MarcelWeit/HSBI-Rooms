package com.example.application.services;

import com.example.application.data.entities.User;
import com.example.application.data.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
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

    public User save(User entity) {
        return repository.save(entity);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public boolean emailExists(String email) {
        return repository.findByUsername(email) != null;
    }

    public User findByUsername(String email) {
        return repository.findByUsername(email); // Oder repository.findByEmail(email), je nach Feldname
    }
}
