package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Component
public class InMemoryUserStorage implements UserStorage {
    private static long currentId;
    private final Map<Long, User> users = new HashMap<>();

    public User create(User user) {
        currentId++;
        user.setId(currentId);
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Boolean contains(User user) {
        return users.containsKey(user.getId());
    }

    @Override
    public Boolean contains(Long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }


}
