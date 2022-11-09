package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.event.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final FeedStorage feedStorage;
    private final Validator<Film> filmValidator;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       GenreStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage,
                       LikeDbStorage likeDbStorage,
                       FeedStorage feedStorage, Validator<Film> filmValidator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.feedStorage = feedStorage;
        this.filmValidator = filmValidator;
    }

    public Film create(Film film) {
        if (filmStorage.contains(film)) {
            log.warn("Film already exist");
            throw new ResourceAlreadyExistException("FilmAlreadyExist");
        }
        filmValidator.check(film);
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
        filmValidator.check(film);
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
        addGenresToFilm(allFilms);
        addLikesToFilm(allFilms);
        return allFilms;
    }

    public void addLike(Long idFilm, Long idUser) {
        CheckFilmAndUser(idFilm, idUser);
        likeDbStorage.addFilmLike(idFilm, idUser);
        feedStorage.createEvent(idUser, Operation.ADD, EventType.LIKE, idFilm);
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
        feedStorage.createEvent(idUser, Operation.REMOVE, EventType.LIKE, idFilm);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.findTop10Films(count);
        addGenresToFilm(popularFilms);
        addLikesToFilm(popularFilms);
        return popularFilms;
    }

    public Film getFilm(Long id) {
        if (!filmStorage.contains(id)) {
            log.warn("Film with id=" + id + " not found");
            throw new ResourceNotFoundException("Film not found");
        }
        Film f = filmStorage.getFilm(id);
        addGenreToFilm(f);
        addLikeToFilm(f);
        return f;
    }


    public List<Film> findTopFilmsByGenreAndYear(int count, int genreId, int year) {
        if (genreId == 0 && year == 0) {
            return getPopularFilms(count);
        }
        List<Film> topNFilms = filmStorage.findTopFilmsByGenreAndYear(count, genreId, year);
        addGenresToFilm(topNFilms);
        addLikesToFilm(topNFilms);
        return topNFilms;
    }

    public List<Film> getCommonFilms(long userId, long friendId) throws ResourceNotFoundException {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        List<Film> films = filmStorage.getByDirector(directorId, sortBy);
        addGenresToFilm(films);
        if (films.size() == 0) {
            throw new ResourceNotFoundException(String.format("Films not found for director: %s", directorId));
        }
        return films;
    }

    public void removeFilm(Long id) {
        if (!filmStorage.contains(id)) {
            throw new ResourceNotFoundException("Film not found");
        }

        if (!filmStorage.removeFilm(id)) {
            throw new RuntimeException("Unexpected error has occurred");
        }
    }

    public List<Film> getByName(String query, List<String> by) {
        if (query == null || query.isEmpty() || by == null || by.isEmpty()) {
            List<Film> films = filmStorage.getByTitleSubstring("");
            addGenresToFilm(films);
            return films;
        }
        query = query.trim().toLowerCase();
        by = by.stream().map(x -> x.trim().toLowerCase()).distinct().collect(Collectors.toList());
        if (by.size() == 1) {
            String byStr = by.get(0);
            if (byStr.equals("director")) {
                List<Film> films = filmStorage.getByDirectorSubstring(query);
                addGenresToFilm(films);
                return films;
            } else if (byStr.equals("title")) {
                List<Film> films = filmStorage.getByTitleSubstring(query);
                addGenresToFilm(films);
                return films;
            }
        } else if (by.size() == 2 && by.containsAll(Arrays.asList("director", "title"))) {
            List<Film> films = filmStorage.getByDirectorOrTitleSubstring(query);
            addGenresToFilm(films);
            return films;
        }
        throw new IllegalArgumentException("by should contain values 'director' or 'title'");
    }

    private void addGenreToFilm(Film film) {
        genreDbStorage.getFilmGenres(film.getId()).forEach(film::addGenre);
    }

    private void addGenresToFilm(List<Film> films) {
        films.forEach(this::addGenreToFilm);
    }

    private void addLikeToFilm(Film film) {
        likeDbStorage.getAllLikes(film.getId()).forEach(film::addLike);
    }

    private void addLikesToFilm(List<Film> films) {
        films.forEach(this::addLikeToFilm);
    }
}
