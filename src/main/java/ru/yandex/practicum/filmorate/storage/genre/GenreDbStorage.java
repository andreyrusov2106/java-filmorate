package ru.yandex.practicum.filmorate.storage.genre;

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
import java.util.Optional;

@Slf4j
@Component()
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String SELECT_GENRE_BY_ID_SQL = "SELECT * FROM PUBLIC.GENRE WHERE GENRE_ID=?";
    private final String SELECT_ALL_GENRES_SQL = "SELECT * FROM PUBLIC.GENRE";
    private final String INSERT_FILM_GENRE_SQL = "INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?,?)";
    private final String DELETE_FILM_GENRE_SQL = "DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID=?";
    private final String SELECT_FILM_GENRE_SQL =
            "SELECT G.GENRE_ID AS GENRE_ID, G.NAME AS NAME  " +
                    "FROM PUBLIC.FILM_GENRE FG LEFT JOIN PUBLIC.GENRE G ON FG.GENRE_ID = G.GENRE_ID " +
                    "WHERE FG.FILM_ID=? ORDER BY GENRE_ID";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenre(long idGenre) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID_SQL, this::mapRowToGenre, idGenre));
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
