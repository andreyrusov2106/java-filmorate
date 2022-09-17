package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Data
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Long> likes;

    public void addLike(long id) {
        if (likes == null) likes = new HashSet<>();
        likes.add(id);
    }

    public void removeLike(long id) {
        if (likes != null) likes.remove(id);
    }
}
