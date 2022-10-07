package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Friend {

    private long friendship_id;
    private final long user_id;
    private final long friend_id;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", user_id);
        values.put("friend_id", friend_id);
        return values;
    }
}
