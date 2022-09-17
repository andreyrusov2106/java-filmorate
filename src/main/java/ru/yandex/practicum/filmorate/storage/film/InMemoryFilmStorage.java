package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
}
