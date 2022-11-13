package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeedStorageImpl implements FeedStorage {

    private static final String CREATE_EVENT =
            "INSERT INTO EVENTS (USER_ID, OPERATION, EVENT_TYPE, ENTITY_ID) VALUES (?, ?, ?, ?)";
    private static final String FEED_BY_USER_ID =
            "SELECT * FROM EVENTS WHERE USER_ID = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> findFeedByUserId(long id) {
        return jdbcTemplate.query(FEED_BY_USER_ID, (rs, rowNum) ->
                Event.builder()
                        .eventId(rs.getLong("id"))
                        .userId(rs.getLong("user_id"))
                        .operation(rs.getString("operation"))
                        .eventType(rs.getString("event_type"))
                        .entityId(rs.getLong("entity_id"))
                        .timestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli())
                        .build(),
                id);
    }

    @Override
    public void createEvent(Long userId, Operation operation, EventType type, Long entityId) {
        jdbcTemplate.update(CREATE_EVENT, userId, operation.name(), type.name(), entityId);
        log.info("User with id {} create some event", userId);
    }
}
