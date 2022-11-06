package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FeedService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public FeedService(@Qualifier("UserDbStorage") UserStorage userStorage, FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public List<Event> findFeedByUserId(long id) {
        if (!userStorage.contains(id)) {
            throw new ResourceNotFoundException(String.format("User with id %s not found", id));
        }
        return feedStorage.findFeedByUserId(id);
    }
}
