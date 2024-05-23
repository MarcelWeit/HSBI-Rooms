package com.example.application.data.dataProvider;

import com.example.application.data.entities.Dozent;
import com.example.application.services.DozentService;
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

public class DozentDataProvider extends AbstractBackEndDataProvider<Dozent, CrudFilter> {

    private final DozentService dozentService;
    private final List<Dozent> dozenten;
    private Consumer<Long> sizeChangeListener;

    public DozentDataProvider(DozentService dozentService) {
        this.dozentService = dozentService;
        dozenten = new ArrayList<>(dozentService.findAll());
    }

    private static Predicate<Dozent> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<Dozent>) dozent -> {
                    try {
                        Object value = valueOf(constraint.getKey(), dozent);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<Dozent> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<Dozent> comparator = Comparator.comparing(
                        dozent -> (Comparable) valueOf(sortClause.getKey(),
                                dozent));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<Dozent>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, Dozent dozent) {
        try {
            Field field = Dozent.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(dozent);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Stream<Dozent> fetchFromBackEnd(Query<Dozent, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<Dozent> stream = dozenten.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<Dozent, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    public void save(Dozent dozent) {
        dozentService.save(dozent);
            if (!(dozent.getId() != 0 && dozenten.stream().anyMatch(d -> d.getId() == dozent.getId()))) {
                dozenten.add(dozent);
            }
        refreshAll(); // Aktualisiere das Grid nach dem Speichern
    }

    public Boolean checkDozentExist(Dozent dozent){
        boolean exists = dozentService.findByVornameAndNachname(dozent.getVorname(), dozent.getNachname())
                .stream()
                .anyMatch(existingDozent -> existingDozent.getId() != dozent.getId());
        return exists;
    }

    public Optional<Dozent> find(Long id) {
        return Optional.ofNullable(dozentService.findById(id));
    }

    public void delete(Dozent dozent) {
        dozentService.delete(dozent);
        dozenten.remove(dozent);
    }
}
