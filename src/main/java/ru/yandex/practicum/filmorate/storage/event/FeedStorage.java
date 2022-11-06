package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    List<Event> findFeedByUserId(long id);
    void createEvent(Long userId, Operation operation, EventType type, Long entityId);

    void removeEventByEntityId(long id);
}
