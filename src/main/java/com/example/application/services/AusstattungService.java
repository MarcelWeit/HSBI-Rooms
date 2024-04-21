package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Room;
import com.example.application.data.repository.AusstattungRepository;
import com.example.application.data.repository.RoomAusstattungRepository;
import com.example.application.data.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AusstattungService {

    private final AusstattungRepository repository;
    private final RoomAusstattungRepository roomAusstattungRepository; // Assuming you have a RoomRepository


    public AusstattungService(AusstattungRepository repository, RoomAusstattungRepository roomRepository) {
        this.repository = repository;
        this.roomAusstattungRepository = roomRepository;
    }

    public Set<Ausstattung> findAll() {
        return Set.copyOf(repository.findAll());
    }

    public void update(Ausstattung entity) {
        repository.save(entity);
    }

    public boolean existsByBez(String bez) {
        return repository.findByBez(bez) != null;
    }

    @Transactional
    public void delete(Ausstattung entity) {
        roomAusstattungRepository.deleteByAusstattung(entity);
        // Now you can delete the Ausstattung
        repository.delete(entity);
    }

}
