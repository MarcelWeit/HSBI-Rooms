package com.example.application.data.repository;

import com.example.application.data.entities.Ausstattung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author marcel weithoener
 */
@Repository
public interface AusstattungRepository extends JpaRepository<Ausstattung, Long>, JpaSpecificationExecutor<Ausstattung> {
    boolean existsByBezEqualsIgnoreCase(String bez);

    Ausstattung findByBez(String bez);
}
