package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Room;
import com.example.application.data.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public Room save(Room entity) {
        return repository.save(entity);
    }

    public boolean existsById(String refNr) {
        return repository.existsById(refNr);
    }

    public List<Room> findAll() {
        return repository.findAll();
    }

    public int countByAusstattungContains(Ausstattung entity) {
        return repository.countByAusstattungContains(entity);
    }

    public Set<Room> findAllByAusstattungContains(Ausstattung entity) {
        return repository.findAllByAusstattungContains(entity);
    }

    public void delete(Room room) {
        repository.delete(room);
    }

    public long countAll() {
        return repository.count();
    }

    public Room findByRefNr(String refNr) {
        return repository.findByRefNr(refNr);
    }

}
