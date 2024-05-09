package com.example.application.data.dataProvider;

import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class VeranstaltungDataProvider extends AbstractBackEndDataProvider<Veranstaltung, CrudFilter> {

    private final VeranstaltungService veranstaltungService;
    private List<Veranstaltung> veranstaltungen;
    private Consumer<Long> sizeChangeListener;

    public VeranstaltungDataProvider(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;
        veranstaltungen = new ArrayList<>(veranstaltungService.findAll());
    }

    @Override
    protected Stream<Veranstaltung> fetchFromBackEnd(Query<Veranstaltung, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<Veranstaltung> stream = veranstaltungen.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<Veranstaltung, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    private static Predicate<Veranstaltung> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<Veranstaltung>) veranstaltung -> {
                    try {
                        Object value = valueOf(constraint.getKey(), veranstaltung);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<Veranstaltung> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<Veranstaltung> comparator = Comparator.comparing(
                        veranstaltung -> (Comparable) valueOf(sortClause.getKey(),
                                veranstaltung));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<Veranstaltung>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, Veranstaltung veranstaltung) {
        try {
            Field field = Veranstaltung.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(veranstaltung);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void save(Veranstaltung veranstaltung) {
        veranstaltungService.save(veranstaltung);
        veranstaltungen = veranstaltungService.findAll();
    }

    Optional<Veranstaltung> find(long id) {
        return Optional.of(veranstaltungService.findById(id));
    }

    public void delete(Veranstaltung veranstaltung) {
        veranstaltungService.delete(veranstaltung);
        veranstaltungen = veranstaltungService.findAll();
    }
}

