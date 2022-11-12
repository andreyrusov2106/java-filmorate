package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.Validator;
import ru.yandex.practicum.filmorate.storage.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.event.Operation;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final FeedStorage feedStorage;
    private final Validator<User> userValidator;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendsStorage friendsStorage, FeedStorage feedStorage, Validator<User> userValidator) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.feedStorage = feedStorage;
        this.userValidator = userValidator;
    }

    public User createUser(User user) {
        userValidator.check(user);
        User createdUser = userStorage.create(user);
        log.info("User created" + createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        userValidator.check(user);
        User updatedUser;
        if (userStorage.contains(user)) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            updatedUser = userStorage.update(user);
            log.info("User updated" + updatedUser);
        } else {
            throw new ResourceNotFoundException("User not found" + user);
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
            throw new ResourceNotFoundException("User with id not found" + id);
        }
    }

    public void addFriend(Long id, Long idFriend) {
        if (!(userStorage.contains(id) && userStorage.contains(idFriend))) {
            throw new ResourceNotFoundException("User with id not found" + id);
        }
        friendsStorage.addFriend(id, idFriend);
        feedStorage.createEvent(id, Operation.ADD, EventType.FRIEND, idFriend);
        log.info(String.format("Added friendship to users with id=%d and id=%d", id, idFriend));
    }

    public void removeFriend(Long id, Long idFriend) {
        if (!(userStorage.contains(id) && userStorage.contains(idFriend))) {
            throw new ResourceNotFoundException("User with id not found" + id);
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
        if (!this.userStorage.contains(id)) {
            throw new ResourceNotFoundException("There is no user with such id");
        }
        List<User> allFriends = friendsStorage.getAllFriends(id);
        log.info(String.format("All friends for user with id=%d is %s", id, allFriends));
        return allFriends;
    }

    public void removeUser(Long id) {
        if (!userStorage.removeUser(id)) {
            throw new ResourceNotFoundException("User not found");
        }
    }
}
