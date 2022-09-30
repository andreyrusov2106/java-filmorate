package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validators.Validator.validateUser;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (userStorage.contains(user)) throw new UserAlreadyExistException("UserAlreadyExist");
        try {
            validateUser(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.create(user);
        log.info("User created" + createdUser);
        return createdUser;
    }

    public User update(User user) {
        try {
            validateUser(user);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        User updatedUser;
        if (userStorage.contains(user)) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            updatedUser = userStorage.update(user);
            log.info("User updated" + updatedUser);
        } else {
            log.warn("User not found" + user);
            throw new ResourceNotFoundException("User not found");
        }
        return updatedUser;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(Long id) {
        if (userStorage.contains(id)) {
            return userStorage.getUser(id);
        } else {
            log.warn("User with id not found" + id);
            throw new ResourceNotFoundException("User not found");
        }
    }

    public void addFriend(Long id, Long idFriend) {
        if (!(userStorage.contains(id) && userStorage.contains(idFriend))) {
            log.warn("User with id not found" + id);
            throw new ResourceNotFoundException("User not found");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(idFriend);
        user.addFriend(idFriend);
        friend.addFriend(id);
        log.info(String.format("Added friendship to users with id=%d and id=%d", id, idFriend));
    }

    public void removeFriend(Long id, Long idFriend) {
        if (!(userStorage.contains(id) && userStorage.contains(idFriend))) {
            log.warn("User with id not found" + id);
            throw new ResourceNotFoundException("User not found");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(idFriend);
        user.removeFriend(idFriend);
        friend.removeFriend(id);
        log.info(String.format("Removed friendship to users with id=%d and id=%d", id, idFriend));
    }

    public List<User> getCommonFriends(Long id, Long idFriend) {
        if (!(userStorage.contains(id) && userStorage.contains(idFriend))) {
            log.warn("User with id not found" + id);
            throw new ResourceNotFoundException("User not found");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(idFriend);
        if (user.getFriends() != null && user.getFriends() != null) {
            Set<Long> userFriends = new HashSet<>(user.getFriends());
            Set<Long> friendFriends = new HashSet<>(friend.getFriends());
            userFriends.retainAll(friendFriends);
            List<User> commonFriends = userFriends.stream().map(userStorage::getUser).collect(Collectors.toList());
            log.info(String.format("Common friends for users with id=%d and id=%d is %s", id, idFriend, commonFriends));
            return commonFriends;
        } else {
            log.info(String.format("Common friends for users with id=%d and id=%d not found", id, idFriend));
            return new ArrayList<>();
        }
    }

    public List<User> getAllFriends(Long id) {
        if (!(userStorage.contains(id))) {
            log.warn("User with id not found" + id);
            throw new ResourceNotFoundException("User not found");
        }
        User user = userStorage.getUser(id);
        Set<Long> userFriends = new HashSet<>(user.getFriends());
        List<User> allFriends = userFriends.stream().map(userStorage::getUser).collect(Collectors.toList());
        log.info(String.format("All friends for user with id=%d is %s", id, allFriends));
        return allFriends;
    }

}
