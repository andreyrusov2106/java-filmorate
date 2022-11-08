package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public interface DirectorFilmStorage {
    List<Director> getByFilm(Long filmId);

    void refresh(Film film);

    boolean delete(Long filmId);
}

