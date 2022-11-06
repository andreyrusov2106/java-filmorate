package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Event {
    @NotNull
    private long eventId;
    @NotNull
    private long userId;
    private String operation;
    private String eventType;
    private long entityId;
    private long timestamp;
}
