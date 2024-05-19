package com.example.application.services;

import com.example.application.data.entities.User;
import com.example.application.data.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
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

    public boolean emailExists(String email) {
        return repository.findByUsername(email) != null;
    }

}
