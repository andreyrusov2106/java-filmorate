package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private long eventId;
    private long userId;
    private String operation;
    private String eventType;
    private long entityId;
    private long timestamp;
}
