package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@Builder
public class Film {
    @NotNull
    private long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Long duration;
    @NotNull
    private Integer rate;
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
    private final Set<Long> likes = new TreeSet<>();
    @NotNull
    private Mpa mpa;
    private List<Director> directors;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating_id", mpa == null ? null : mpa.getId());
        return values;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addLike(long idUser) {
        likes.add(idUser);
    }

    public void removeLike(long idUser) {
        likes.remove(idUser);
    }

    public void setDirectors(List<Director> list) {
        this.directors = list;
    }
}