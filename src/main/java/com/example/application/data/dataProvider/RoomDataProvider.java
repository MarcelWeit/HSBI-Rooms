package com.example.application.data.dataProvider;

import com.example.application.data.entities.Room;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Ein DataProvider für die Räume, der die Daten aus dem Backend abruft.
 *
 * @author marcel.weithoener, verändert nach Vaadin
 */
public class RoomDataProvider extends AbstractBackEndDataProvider<Room, CrudFilter> {

    private final RoomService roomService;
    private final List<Room> rooms;

    /**
     * Erzeugt einen neuen RoomDataProvider.
     *
     * @param roomService der Service, der die Räume verwaltet
     */
    public RoomDataProvider(RoomService roomService) {
        this.roomService = roomService;
        rooms = new ArrayList<>(roomService.findAll());
    }

    /**
     * Erzeugt ein Predicate auf der Grundlage der angegebenen Filter.
     *
     * @param filter der Filter, der die Einschränkungen definiert
     * @return ein Predicate, das die Einschränkungen des Filters erfüllt
     */
    private static Predicate<Room> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<Room>) room -> {
                    try {
                        Object value = valueOf(constraint.getKey(), room);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    /**
     * Erzeugt einen Komparator auf der Grundlage der angegebenen Sortierreihenfolge.
     *
     * @param filter
     * @return
     */
    private static Comparator<Room> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<Room> comparator = Comparator.comparing(
                        room -> (Comparable) valueOf(sortClause.getKey(),
                                room));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<Room>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    /**
     * Gibt den Wert des angegebenen Felds für den Raum zurück.
     *
     * @param fieldName der Name des Felds
     * @param room      der Raum, dessen Feldwert zurückgegeben werden soll
     * @return den Wert des Felds
     */

    private static Object valueOf(String fieldName, Room room) {
        try {
            Field field = Room.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(room);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Holt die Daten auf der Grundlage der Abfrage aus dem Backend.
     *
     * @param query die Abfrage, die das Sortieren, Filtern und Paging für die Daten enthält
     * @return eine Liste von Elementen, die den Kriterien der Abfrage entsprechen
     */
    @Override
    protected Stream<Room> fetchFromBackEnd(Query<Room, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<Room> stream = rooms.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    /**
     * @param query die Abfrage, die die Filterung definiert, die für die Anzahl der Elemente verwendet werden soll
     * @return die Anzahl der Elemente, die den Filterkriterien entsprechen
     */
    @Override
    protected int sizeInBackEnd(Query<Room, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        return (int) count;
    }

    /**
     * Speichert den Raum im Backend und aktualisiert die Liste der Räume.
     *
     * @param room der zu speichernde Raum
     */
    public void save(Room room) {
        roomService.save(room);
        rooms.add(room);
    }

    /**
     * Löscht den Raum aus dem Backend und aktualisiert die Liste der Räume.
     *
     * @param room der zu löschende Raum
     */
    public void delete(Room room) {
        roomService.delete(room);
        rooms.remove(room);
    }
}
