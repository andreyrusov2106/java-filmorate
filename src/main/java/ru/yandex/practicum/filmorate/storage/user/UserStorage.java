package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User getUser(Long id);

    List<User> findAll();

    Boolean contains(User user);

    Boolean contains(Long id);

}
