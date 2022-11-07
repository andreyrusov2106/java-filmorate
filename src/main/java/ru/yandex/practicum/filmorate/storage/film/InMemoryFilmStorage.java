package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Qualifier
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static long currentId;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        currentId++;
        film.setId(currentId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> findTop10Films(int count) {
        return new ArrayList<>();
    }

    @Override
    public Boolean contains(Film film) {
        return films.containsKey(film.getId());
    }

    @Override
    public Boolean contains(Long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> findTopFilmsByGenreAndYear(int count, int genreId, int year) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "method not implemented");
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "method not implemented");
    }

    public List<Film> getByDirector(Long directorId, String sortBy) {
        throw new InternalServerException("Method not allowed");
    }
}
