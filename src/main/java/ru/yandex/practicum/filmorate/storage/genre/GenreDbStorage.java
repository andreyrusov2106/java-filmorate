package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component()
@Qualifier("GenreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String SELECT_GENRE_BY_ID_SQL = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    private final static String SELECT_ALL_GENRES_SQL = "SELECT * FROM GENRE";
    private final static String INSERT_FILM_GENRE_SQL = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?, ?)";
    private final static String DELETE_FILM_GENRE_SQL = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
    private final static String SELECT_FILM_GENRE_SQL =
            "SELECT G.GENRE_ID AS GENRE_ID, G.NAME AS NAME  " +
                    "FROM FILM_GENRE FG LEFT JOIN GENRE G ON FG.GENRE_ID = G.GENRE_ID " +
                    "WHERE FG.FILM_ID = ? ORDER BY GENRE_ID";

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenre(long idGenre) {
        try {
            return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID_SQL,
                    this::mapRowToGenre, idGenre)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getFilmGenres(Long idFilm) {
        try {
            return jdbcTemplate.query(SELECT_FILM_GENRE_SQL, this::mapRowToGenre, idFilm);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public void addFilmGenre(long idFilm, long idGenre) {
        jdbcTemplate.update(INSERT_FILM_GENRE_SQL,
                idFilm,
                idGenre);
    }

    @Override
    public void removeFilmAllGenre(long idFilm) {
        jdbcTemplate.update(DELETE_FILM_GENRE_SQL,
                idFilm);
    }

}
