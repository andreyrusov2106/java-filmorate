package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

@Repository
public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> findAll();

    List<Film> findTop10Films(int count);

    Boolean contains(Film film);

    Boolean contains(Long id);

    Film getFilm(Long id);

    List<Film> findTopFilmsByGenreAndYear(int count, int genreId, int year);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getByDirector(Long directorId, String sortBy);

    boolean removeFilm(Long id);

    List<Film> getByTitleSubstring(String substring);

    List<Film> getByDirectorSubstring(String substring);

    List<Film> getByDirectorOrTitleSubstring(String substring);
}
