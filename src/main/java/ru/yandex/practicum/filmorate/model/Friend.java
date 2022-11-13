package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Friend {

    private long friendshipId;
    private final long userId;
    private final long friendId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", userId);
        values.put("friend_id", friendId);
        return values;
    }
}
