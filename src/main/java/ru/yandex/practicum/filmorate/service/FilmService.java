package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validator.Validator;
import ru.yandex.practicum.filmorate.storage.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.event.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
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
    private final LikeDbStorage likeDbStorage;
    private final FeedStorage feedStorage;
    private final Validator<Film> filmValidator;

    @Autowired
    public FilmService (@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       GenreStorage genreDbStorage,
                       LikeDbStorage likeDbStorage,
                       FeedStorage feedStorage,
                       Validator<Film> filmValidator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.feedStorage = feedStorage;
        this.filmValidator = filmValidator;
    }

    public Film createFilm(Film film) {
        if (filmStorage.contains(film)) {
            throw new ResourceAlreadyExistException("FilmAlreadyExist");
        }
        filmValidator.check(film);
        Film createdFilm = filmStorage.create(film);
        if (!film.getGenres().isEmpty()) {
            genreDbStorage.removeFilmAllGenre(film.getId());
            film.getGenres()
                    .forEach(genre -> genreDbStorage.addFilmGenre(film.getId(), genre.getId()));
        }
        log.info("Film created" + film);
        return createdFilm;
    }

    public Film updateFilm(Film film) {
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
            throw new ResourceNotFoundException("Film not found" + film);
        }
        return updatedFilm;
    }

    public List<Film> findAll() {
        List<Film> allFilms = filmStorage.findAll();
        addGenresToFilms(allFilms);
        addLikesToFilms(allFilms);
        return allFilms;
    }

    public void addLike(Long idFilm, Long idUser) {
        checkFilmAndUser(idFilm, idUser);
        likeDbStorage.addFilmLike(idFilm, idUser);
        feedStorage.createEvent(idUser, Operation.ADD, EventType.LIKE, idFilm);
    }

    private void checkFilmAndUser(Long idFilm, Long idUser) {
        if (!filmStorage.contains(idFilm)) {
            throw new ResourceNotFoundException("Film with id=" + idFilm + " not found");
        }
        if (!userStorage.contains(idUser)) {
            throw new ResourceNotFoundException("User with id=" + idUser + " not found");
        }
        filmStorage.getFilm(idFilm);
    }

    public void removeLike(Long idFilm, Long idUser) {
        checkFilmAndUser(idFilm, idUser);
        likeDbStorage.removeFilmLike(idFilm, idUser);
        feedStorage.createEvent(idUser, Operation.REMOVE, EventType.LIKE, idFilm);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.findTop10Films(count);
        addGenresToFilms(popularFilms);
        addLikesToFilms(popularFilms);
        return popularFilms;
    }

    public Film getFilm(Long id) {
        Film f = filmStorage.getFilm(id);
        addGenresToFilm(f);
        addLikesToFilm(f);
        return f;
    }

    public List<Film> findTopFilmsByGenreAndYear(int count, int genreId, int year) {
        if (genreId == 0 && year == 0) {
            return getPopularFilms(count);
        }
        List<Film> topNFilms = filmStorage.findTopFilmsByGenreAndYear(count, genreId, year);
        addGenresToFilms(topNFilms);
        addLikesToFilms(topNFilms);
        return topNFilms;
    }

    public List<Film> getCommonFilms(long userId, long friendId) throws ResourceNotFoundException {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        List<Film> films = filmStorage.getByDirector(directorId, sortBy);
        addGenresToFilms(films);
        if (films.size() == 0) {
            throw new ResourceNotFoundException(String.format("Films not found for director: %s", directorId));
        }
        return films;
    }

    public void removeFilm(Long id) {
        if (!filmStorage.removeFilm(id)) {
            throw new ResourceNotFoundException("Film not found");
        }
    }

    public List<Film> getByName(String query, List<String> by) {
        if (query == null || query.isEmpty() || by == null || by.isEmpty()) {
            List<Film> films = filmStorage.getByTitleSubstring("");
            addGenresToFilms(films);
            return films;
        }
        query = query.trim().toLowerCase();
        by = by.stream().map(x -> x.trim().toLowerCase()).distinct().collect(Collectors.toList());
        if (by.size() == 1) {
            String byStr = by.get(0);
            if (byStr.equals("director")) {
                List<Film> films = filmStorage.getByDirectorSubstring(query);
                addGenresToFilms(films);
                return films;
            } else if (byStr.equals("title")) {
                List<Film> films = filmStorage.getByTitleSubstring(query);
                addGenresToFilms(films);
                return films;
            }
        } else if (by.size() == 2 && by.containsAll(Arrays.asList("director", "title"))) {
            List<Film> films = filmStorage.getByDirectorOrTitleSubstring(query);
            addGenresToFilms(films);
            return films;
        }
        throw new IllegalArgumentException("by should contain values 'director' or 'title'");
    }

    private void addGenresToFilm(Film film) {
        genreDbStorage.getFilmGenres(film.getId()).forEach(film::addGenre);
    }

    private void addGenresToFilms(List<Film> films) {
        films.forEach(this::addGenresToFilm);
    }

    private void addLikesToFilm(Film film) {
        likeDbStorage.getAllLikes(film.getId()).forEach(film::addLike);
    }

    private void addLikesToFilms(List<Film> films) {
        films.forEach(this::addLikesToFilm);
    }
}
