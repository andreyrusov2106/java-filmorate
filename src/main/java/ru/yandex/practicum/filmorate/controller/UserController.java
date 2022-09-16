package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validators.Validator.validateUser;

@Slf4j
@RestController
public class UserController {
    private static int currentId;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        if (users.containsKey(user.getId())) throw new UserAlreadyExistException("UserAlreadyExist");
        try {
            validateUser(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        currentId++;
        user.setId(currentId);
        users.put(user.getId(), user);
        log.info("User created" + user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        try {
            validateUser(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        if (users.containsKey(user.getId())) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("User updated" + user);
        } else {
            log.info("User not found" + user);
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }
}
