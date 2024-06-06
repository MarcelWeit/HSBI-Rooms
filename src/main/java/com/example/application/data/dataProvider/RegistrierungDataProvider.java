package com.example.application.data.dataProvider;

import com.example.application.data.entities.Registrierung;
import com.example.application.services.FreischaltenService;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RegistrierungDataProvider extends AbstractBackEndDataProvider<Registrierung, CrudFilter> {

    private final FreischaltenService freischaltenService;
    private List<Registrierung> registrations;

    public RegistrierungDataProvider(FreischaltenService freischaltenService) {
        this.freischaltenService = freischaltenService;
        this.registrations = freischaltenService.findAllRegistrierungen();
    }

    private static Object valueOf(String fieldName, Registrierung registration) {
        try {
            var field = Registrierung.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(registration);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void update() {
        this.registrations = freischaltenService.findAllRegistrierungen();
    }

    @Override
    protected Stream<Registrierung> fetchFromBackEnd(Query<Registrierung, CrudFilter> query) {
        Stream<Registrierung> stream = registrations.stream();

        if (query.getFilter().isPresent()) {
            Predicate<Registrierung> predicate = createPredicate(query.getFilter().get());
            stream = stream.filter(predicate);
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    protected int sizeInBackEnd(Query<Registrierung, CrudFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    private Predicate<Registrierung> createPredicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<Registrierung>) registration -> {
                    try {
                        Object value = valueOf(constraint.getKey(), registration);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    public void save(Registrierung registration) {
        freischaltenService.save(registration);
    }

    public void delete(Registrierung registration) {
        freischaltenService.delete(registration);
    }
}
