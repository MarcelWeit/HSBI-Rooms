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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RoomDataProvider extends AbstractBackEndDataProvider<Room, CrudFilter> {

    private final RoomService roomService;
    private List<Room> rooms;
    private Consumer<Long> sizeChangeListener;

    public RoomDataProvider(RoomService roomService) {
        this.roomService = roomService;
        rooms = new ArrayList<>(roomService.findAll());
    }

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

    @Override
    protected int sizeInBackEnd(Query<Room, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

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

    private static Object valueOf(String fieldName, Room room) {
        try {
            Field field = Room.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(room);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void save(Room room) {
        roomService.save(room);
        rooms = roomService.findAll();
    }

    Optional<Room> find(String refNr) {
        return Optional.of(roomService.findByRefNr(refNr));
    }

    public void delete(Room room) {
        roomService.delete(room);
        rooms = roomService.findAll();
    }
}
