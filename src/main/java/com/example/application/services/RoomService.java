package com.example.application.services;

import com.example.application.data.entities.Room;
import com.example.application.data.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public Room update(Room entity) {
        return repository.save(entity);
    }

    public boolean existsById(String refNr) {
        return repository.existsById(refNr);
    }

    public Set<Room> findAll() {
        return Set.copyOf(repository.findAll());
    }
}
