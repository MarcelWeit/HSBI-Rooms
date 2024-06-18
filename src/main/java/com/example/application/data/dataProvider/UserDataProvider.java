package com.example.application.data.dataProvider;

import com.example.application.data.entities.User;
import com.example.application.services.UserService;
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

public class UserDataProvider extends AbstractBackEndDataProvider<User, CrudFilter> {

    private final UserService userService;
    private final List<User> users;
    private Consumer<Long> sizeChangeListener;

    public UserDataProvider(UserService userService, boolean fetchLockedUsers) {
        this.userService = userService;
        users = new ArrayList<>(userService.findAll());
    }

    private static Predicate<User> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<User>) user -> {
                    try {
                        Object value = valueOf(constraint.getKey(), user);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<User> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<User> comparator = Comparator.comparing(
                        room -> (Comparable) valueOf(sortClause.getKey(), room));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<User>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, User user) {
        try {
            Field field = User.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(user);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // Fetchen der Daten
    @Override
    protected Stream<User> fetchFromBackEnd(Query<User, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<User> stream = users.stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<User, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    void setSizeChangeListener(Consumer<Long> listener) {
        sizeChangeListener = listener;
    }

    // Speichen eines neuen Nutzers
    public void save(User user) {
        Optional<User> existingUser = users.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst();

        if (existingUser.isPresent()) {
            // Existierenden User Updaten
            User userToUpdate = existingUser.get();
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setFachbereich(user.getFachbereich());
            userToUpdate.setRoles(user.getRoles());
            userService.update(userToUpdate);
        } else {
            // Save new user
            userService.save(user);
            users.add(user);
        }
    }

    Optional<User> find(String username) {
        return Optional.of(userService.findByUsername(username));
    }

    public void delete(User user) {
        userService.delete(user);
        users.remove(user);
    }
}
