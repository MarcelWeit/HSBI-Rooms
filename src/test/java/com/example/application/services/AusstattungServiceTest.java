package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.repository.AusstattungRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testet alle Methoden der AusstattungService Klasse
 *
 * @author Marcel Weithoener
 */
@ExtendWith(MockitoExtension.class)
class AusstattungServiceTest {

    @Mock
    private AusstattungRepository ausstattungRepository;

    @InjectMocks
    private AusstattungService ausstattungService;

    @Test
    void findAll() {
        List<Ausstattung> ausstattungListToTest = new ArrayList<>();
        Ausstattung a1 = new Ausstattung("Beamer");
        Ausstattung a2 = new Ausstattung("Whiteboard");
        ausstattungListToTest.add(a1);
        ausstattungListToTest.add(a2);
        when(ausstattungRepository.findAll()).thenReturn(ausstattungListToTest);
        List<Ausstattung> ausstattungListReturned = new ArrayList<>(ausstattungService.findAll());
        assertThat(ausstattungListReturned).containsExactlyInAnyOrder(a1, a2);
    }

    @Test
    void save() {
        Ausstattung ausstattungToTest = new Ausstattung("Beamer");
        when(ausstattungRepository.save(ausstattungToTest)).thenReturn(ausstattungToTest);
        Ausstattung ausstattungReturned = ausstattungService.save(ausstattungToTest);
        assertThat(ausstattungReturned).isNotNull();
        assertThat(ausstattungReturned).isEqualTo(ausstattungToTest);
    }

    @Test
    void existsByBezEqualsIgnoreCase() {
        Ausstattung ausstattungToTest = new Ausstattung("Beamer");
        when(ausstattungRepository.existsByBezEqualsIgnoreCase(ausstattungToTest.getBez())).thenReturn(true);
        ausstattungService.save(ausstattungToTest);
        assertThat(ausstattungService.existsByBezEqualsIgnoreCase("Beamer")).isTrue();
    }

    @Test
    void delete() {
        Ausstattung ausstattungToTest = new Ausstattung("Beamer");
        ausstattungService.save(ausstattungToTest);
        ausstattungService.delete(ausstattungToTest);
        assertThat(ausstattungService.existsByBezEqualsIgnoreCase("Beamer")).isFalse();
    }
}