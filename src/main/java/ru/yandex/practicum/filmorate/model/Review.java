package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class Review {
    private long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private long useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("review_id", reviewId);
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }

}
