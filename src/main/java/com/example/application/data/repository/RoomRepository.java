package com.example.application.data.repository;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface RoomRepository extends JpaRepository<Room, String>, JpaSpecificationExecutor<Room>{
    Set<Room> findAllByAusstattungContains(Ausstattung entity);
    int countByAusstattungContains(Ausstattung entity);
    Room findByRefNr(String refNr);
}
