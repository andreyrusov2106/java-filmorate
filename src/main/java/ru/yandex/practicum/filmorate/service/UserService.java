package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.event.Operation;

import java.util.List;

import static ru.yandex.practicum.filmorate.validators.Validator.validateUser;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendsStorage friendsStorage, FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.feedStorage = feedStorage;
    }

    public User create(User user) {
        //if (userStorage.contains(user)) throw new UserAlreadyExistException("UserAlreadyExist");
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
        friendsStorage.addFriend(id, idFriend);
        feedStorage.createEvent(id, Operation.ADD, EventType.FRIEND, idFriend);
        log.info(String.format("Added friendship to users with id=%d and id=%d", id, idFriend));
    }

    public void removeFriend(Long id, Long idFriend) {
        if (!(userStorage.contains(id) && userStorage.contains(idFriend))) {
            log.warn("User with id not found" + id);
            throw new ResourceNotFoundException("User not found");
        }
        friendsStorage.removeFriend(id, idFriend);
        feedStorage.createEvent(id, Operation.REMOVE, EventType.FRIEND, idFriend);
        log.info(String.format("Removed friendship to users with id=%d and id=%d", id, idFriend));
    }

    public List<User> getCommonFriends(Long id, Long idFriend) {
        List<User> commonFriends = friendsStorage.getCommonFriends(id, idFriend);
        log.info(String.format("Common friends for users with id=%d and id=%d is %s", id, idFriend, commonFriends));
        return commonFriends;
    }

    public List<User> getAllFriends(Long id) {
        List<User> allFriends = friendsStorage.getAllFriends(id);
        log.info(String.format("All friends for user with id=%d is %s", id, allFriends));
        return allFriends;
    }
}
