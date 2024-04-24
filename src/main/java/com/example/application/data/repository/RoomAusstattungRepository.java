package com.example.application.data.repository;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomAusstattungRepository extends JpaRepository<Room, Ausstattung> {
    void deleteByAusstattung(Ausstattung ausstattung);

    int countByAusstattungContains(Ausstattung ausstattung);
}
