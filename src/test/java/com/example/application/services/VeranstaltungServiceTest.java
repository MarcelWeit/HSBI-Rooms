package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Fachbereich;
import com.example.application.repository.VeranstaltungRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class VeranstaltungServiceTest {

    @Mock
    private VeranstaltungRepository veranstaltungRepository;

    @InjectMocks
    private VeranstaltungService veranstaltungService;


    @Test
    void save() {
        Dozent dummy = new Dozent("Mustermann", "Max", Fachbereich.WIRTSCHAFT);
        Veranstaltung testData = new Veranstaltung("12WIP2024", "Mathematik für Ökonomen", dummy, 120, Fachbereich.WIRTSCHAFT);
        when(veranstaltungRepository.save(testData)).thenReturn(testData);

        Veranstaltung returnData = veranstaltungService.save(testData);


        assertThat(returnData).isNotNull();
        assertThat(returnData).isEqualTo(testData);
    }

    @Test
    void delete() {
        Dozent dummy = new Dozent("Mustermann", "Max", Fachbereich.WIRTSCHAFT);
        Veranstaltung testData = new Veranstaltung("12WIP2024", "Mathematik für Ökonomen", dummy, 120, Fachbereich.WIRTSCHAFT);

        veranstaltungService.save(testData);

        veranstaltungService.delete(testData);

        assertThat(veranstaltungService.findById("12WIP2024")).isEmpty();
    }

    @Test
    void findById() {
        Dozent dummy = new Dozent("Mustermann", "Max", Fachbereich.WIRTSCHAFT);
        Veranstaltung testData = new Veranstaltung("12WIP2024", "Mathematik für Ökonomen", dummy, 120, Fachbereich.WIRTSCHAFT);
        when(veranstaltungRepository.findById("12WIP2024")).thenReturn(Optional.of(testData));

        veranstaltungService.save(testData);

        assertThat(veranstaltungService.findById("12WIP2024")).isPresent();
    }

    @Test
    void findAll() {
        List< Veranstaltung> testList = new ArrayList<>();
        Dozent dummy = new Dozent("Mustermann", "Max", Fachbereich.WIRTSCHAFT);
        Veranstaltung testData = new Veranstaltung("12WIP2024", "Mathematik für Ökonomen", dummy, 120, Fachbereich.WIRTSCHAFT);
        Veranstaltung testData2 = new Veranstaltung("5WIP2024", "Mathematik für Wirtschaftsinformatiker", dummy, 120, Fachbereich.WIRTSCHAFT);
        testList.add(testData);
        testList.add(testData2);
        when(veranstaltungRepository.findAll()).thenReturn(testList);

        veranstaltungService.save(testData);
        veranstaltungService.save(testData2);

        List<Veranstaltung> returnList = new ArrayList<>(veranstaltungService.findAll());

        assertThat(returnList).isEqualTo(testList);

    }
}