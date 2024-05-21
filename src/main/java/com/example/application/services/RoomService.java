package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Room;
import com.example.application.data.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author marcel weithoener
 */
@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public void save(Room entity) {
        repository.save(entity);
    }

    public List<Room> findAll() {
        return repository.findAll();
    }

    public boolean refNrExists(String refNr) {
        return repository.findByRefNr(refNr).isPresent();
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

    public Optional<Room> findByRefNr(String refNr) {
        return repository.findByRefNr(refNr);
    }

}
