package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.util.List;

public interface GenreStorage {

    List<Genre> getAllGenres();

    Genre getGenre(long idGenre);

    List<Genre> getFilmGenres(Long idFilm);

    void addFilmGenre(long film_id, long genre_id);

    void removeFilmAllGenre(long film_id);

}
