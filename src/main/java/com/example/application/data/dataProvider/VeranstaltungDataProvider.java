package com.example.application.data.dataProvider;

import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class VeranstaltungDataProvider extends AbstractBackEndDataProvider<Veranstaltung, CrudFilter> {

    private VeranstaltungService veranstaltungService;
    private final List<Veranstaltung> veranstaltungen;
    private Consumer<Long> sizeChangeListener;

    public VeranstaltungDataProvider(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;
        veranstaltungen = new ArrayList<>(veranstaltungService.findAll());
    }

    @Override
    protected Stream<Veranstaltung> fetchFromBackEnd(Query<Veranstaltung, CrudFilter> query) {
        return Stream.empty();
    }

    @Override
    protected int sizeInBackEnd(Query<Veranstaltung, CrudFilter> query) {
        return 0;
    }
    public void saveVeranstaltung(Veranstaltung veranstaltung) {
        veranstaltungService.save(veranstaltung);
        veranstaltungen.add(veranstaltung);
    }
    public void deleteVeranstaltung(Veranstaltung veranstaltung) {
        veranstaltungService.delete(veranstaltung);
        veranstaltungen.remove(veranstaltung);
    }
}
