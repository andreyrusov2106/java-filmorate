package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validators.Validator.validateFilm;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        if (filmStorage.contains(film)) {
            log.warn("Film already exist");
            throw new FilmAlreadyExistException("FilmAlreadyExist");
        }
        try {
            validateFilm(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        Film createdFilm = filmStorage.create(film);
        log.info("Film created" + film);
        return createdFilm;
    }

    public Film update(Film film) {
        try {
            validateFilm(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        Film updatedFilm;
        if (filmStorage.contains(film)) {
            updatedFilm = filmStorage.update(film);
            log.info("Film updated" + film);
        } else {
            log.warn("Film not found" + film);
            throw new ResourceNotFoundException("Film not found");
        }
        return updatedFilm;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(Long idFilm, Long idUser) {
        Film film = CheckFilmAndUser(idFilm, idUser);
        film.addLike(idUser);
    }

    private Film CheckFilmAndUser(Long idFilm, Long idUser) {
        if (!filmStorage.contains(idFilm)) {
            log.warn("Film with id=" + idFilm + " not found");
            throw new ResourceNotFoundException("Film not found");
        }
        if (!userStorage.contains(idUser)) {
            log.warn("User with id=" + idUser + " not found");
            throw new ResourceNotFoundException("User not found");
        }
        return filmStorage.getFilm(idFilm);
    }

    public void removeLike(Long idFilm, Long idUser) {
        Film film = CheckFilmAndUser(idFilm, idUser);
        film.removeLike(idUser);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.findAll();
        films.sort(Comparator.comparingInt((Film f) ->
        {
            if (f.getLikes() == null) return 0;
            return f.getLikes().size();
        }).reversed());
        List<Film> popularFilms = films.stream().limit(count).collect(Collectors.toList());
        log.info(String.format("Top %d popular films is %s", count, popularFilms));
        return popularFilms;
    }

    public Film getFilm(Long id) {
        if (!filmStorage.contains(id)) {
            log.warn("Film with id=" + id + " not found");
            throw new ResourceNotFoundException("Film not found");
        }
        return filmStorage.getFilm(id);
    }


}
