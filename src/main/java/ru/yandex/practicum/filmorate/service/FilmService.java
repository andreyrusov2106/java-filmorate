package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.validators.Validator.validateFilm;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeDbStorage likeDbStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       GenreStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage,
                       LikeDbStorage likeDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.likeDbStorage = likeDbStorage;

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
        if (mpaDbStorage.getMpa(film.getMpa().getId()).isEmpty()) {
            log.warn("Mpa with id=" + film.getMpa().getId() + " not found");
            throw new ResourceNotFoundException("Mpa not found");
        }
        Film createdFilm = filmStorage.create(film);
        if (film.getGenres() != null) {
            genreDbStorage.removeFilmAllGenre(film.getId());
            film.getGenres()
                    .forEach((Genre idGenre) -> genreDbStorage.addFilmGenre(film.getId(), idGenre.getId()));
        }
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
            if (film.getGenres() != null) {
                genreDbStorage.removeFilmAllGenre(film.getId());
                film.getGenres()
                        .forEach(idGenre -> genreDbStorage.addFilmGenre(film.getId(), idGenre.getId()));
                updatedFilm.getGenres()
                        .forEach(genre -> genre.setName(genreDbStorage.getGenre(genre.getId()).orElseThrow().getName()));
            }
            log.info("Film updated" + film);
        } else {
            log.warn("Film not found" + film);
            throw new ResourceNotFoundException("Film not found");
        }
        return updatedFilm;
    }

    public List<Film> findAll() {
        List<Film> allFilms = filmStorage.findAll();
        allFilms.forEach(f -> genreDbStorage.getFilmGenres(f.getId()).forEach(f::addGenre));
        return allFilms;
    }

    public void addLike(Long idFilm, Long idUser) {
        CheckFilmAndUser(idFilm, idUser);
        likeDbStorage.addFilmLike(idFilm, idUser);
    }

    private void CheckFilmAndUser(Long idFilm, Long idUser) {
        if (!filmStorage.contains(idFilm)) {
            log.warn("Film with id=" + idFilm + " not found");
            throw new ResourceNotFoundException("Film not found");
        }
        if (!userStorage.contains(idUser)) {
            log.warn("User with id=" + idUser + " not found");
            throw new ResourceNotFoundException("User not found");
        }
        filmStorage.getFilm(idFilm);
    }

    public void removeLike(Long idFilm, Long idUser) {
        CheckFilmAndUser(idFilm, idUser);
        likeDbStorage.removeFilmLike(idFilm, idUser);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.findTop10Films(count);
        popularFilms.forEach(f -> genreDbStorage.getFilmGenres(f.getId()).forEach(f::addGenre));
        log.info(String.format("Top %d popular films is %s", count, popularFilms));
        return popularFilms;
    }

    public Film getFilm(Long id) {
        if (!filmStorage.contains(id)) {
            log.warn("Film with id=" + id + " not found");
            throw new ResourceNotFoundException("Film not found");
        }
        Film f = filmStorage.getFilm(id);
        genreDbStorage.getFilmGenres(f.getId()).forEach(f::addGenre);
        return f;
    }
}
