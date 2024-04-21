package com.example.application.services;

import com.example.application.data.entities.Room;
import com.example.application.data.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public Room update(Room entity) {
        return repository.save(entity);
    }

    public boolean existsById(long refNr) {
        return repository.existsById(refNr);
    }
}
