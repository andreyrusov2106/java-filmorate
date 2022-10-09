package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> findAll();

    List<Film> findTop10Films(int count);

    Boolean contains(Film film);

    Boolean contains(Long id);

    Film getFilm(Long id);
}
