package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Anrede;
import com.example.application.data.enums.Fachbereich;
import com.example.application.repository.DozentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * Testet alle Methoden der DozentService Klasse
 */
@ExtendWith(MockitoExtension.class)
class DozentServiceTest {

    @Mock
    private DozentRepository dozentRepository;

    @InjectMocks
    private DozentService dozentService;

    @Test
    void findAll() {
        List<Dozent> dozentListToTest = new ArrayList<>();
        Dozent d1 = new Dozent(Anrede.HERR, "Mustermann", "Max", Fachbereich.WIRTSCHAFT, "Dr.");
        Dozent d2 = new Dozent(Anrede.FRAU, "Mustermann", "Erika", Fachbereich.WIRTSCHAFT, "Prof.");
        dozentListToTest.add(d1);
        dozentListToTest.add(d2);
        when(dozentRepository.findAll()).thenReturn(dozentListToTest);
        List<Dozent> dozentListReturned = new ArrayList<>(dozentService.findAll());
        assertThat(dozentListReturned.size()).isEqualTo(2);
        assertThat(dozentListReturned).isNotNull();
        assertThat(dozentListReturned).isEqualTo(dozentListToTest);
    }

    @Test
    void findByVornameAndNachname() {
        Dozent dozentToTest = new Dozent(Anrede.HERR, "Mustermann", "Max", Fachbereich.WIRTSCHAFT, "Dr.");
        when(dozentRepository.findByVornameAndNachname("Max", "Mustermann")).thenReturn(Optional.of(dozentToTest));
        Optional<Dozent> dozentFound = dozentService.findByVornameAndNachname("Max", "Mustermann");
        assertThat(dozentFound).isPresent();
        assertThat(dozentFound.get()).isEqualTo(dozentToTest);
    }

    @Test
    void save() {
        Dozent dozentToTest = new Dozent(Anrede.HERR, "Mustermann", "Max", Fachbereich.WIRTSCHAFT, "Dr.");
        when(dozentRepository.save(dozentToTest)).thenReturn(dozentToTest);
        Dozent dozentReturned = dozentService.save(dozentToTest);
        assertThat(dozentReturned).isNotNull();
        assertThat(dozentReturned).isEqualTo(dozentToTest);
    }

    @Test
    void delete() {
        Dozent dozentToTest = new Dozent(Anrede.HERR, "Mustermann", "Max", Fachbereich.WIRTSCHAFT, "Dr.");
        when(dozentRepository.save(dozentToTest)).thenReturn(dozentToTest);
        when(dozentRepository.findByVornameAndNachname("Max", "Mustermann")).thenReturn(Optional.of(dozentToTest));
        dozentService.save(dozentToTest);
        dozentService.delete(dozentToTest);
        when(dozentRepository.findByVornameAndNachname("Max", "Mustermann")).thenReturn(Optional.empty());
        Optional<Dozent> dozentFound = dozentService.findByVornameAndNachname("Max", "Mustermann");
        assertThat(dozentFound).isNotPresent();
    }
}
