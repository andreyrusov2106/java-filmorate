package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

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
    private Timestamp timestamp;
}
