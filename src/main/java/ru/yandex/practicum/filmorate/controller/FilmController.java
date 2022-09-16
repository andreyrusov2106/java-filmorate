package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validators.Validator.validateFilm;

@Slf4j
@RestController
public class FilmController {
    private static int currentId;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("FilmAlreadyExist");
            throw new FilmAlreadyExistException("FilmAlreadyExist");
        }
        try {
            validateFilm(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        currentId++;
        film.setId(currentId);
        films.put(film.getId(), film);
        log.info("Film created" + film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        try {
            validateFilm(film);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film updated" + film);
        } else {
            log.info("Film not found" + film);
            throw new ResourceNotFoundException("Film not found");
        }
        return film;
    }
}
