package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component()
@Qualifier("DirectorFilmDbStorage")
@RequiredArgsConstructor
public class DirectorFilmDbStorage implements DirectorFilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String SELECT_BY_FILM_SQL =
            "SELECT d.director_id, d.name FROM director_films df " +
                    "JOIN directors d ON df.director_id = d.director_id " +
                    "WHERE df.film_id = ?";
    private final static String INSERT_SQL = "INSERT INTO director_films (director_id, film_id) VALUES ";
    private final static String DELETE_SQL = "DELETE FROM director_films WHERE film_id = ?";

    @Override
    public List<Director> getByFilm(Long filmId) {
        return jdbcTemplate.query(SELECT_BY_FILM_SQL, this::mapDirector, filmId);
    }

    @Override
    public void refresh(Film film) {
        delete(film.getId());
        if (film.getDirectors() != null && film.getDirectors().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Director director : film.getDirectors()) {
                sb.append("(").append(director.getId()).append(",").append(film.getId()).append("),");
            }
            System.out.println(sb.substring(0, sb.length() - 1));
            jdbcTemplate.update(INSERT_SQL + sb.substring(0, sb.length() - 1));
        }
    }

    @Override
    public void delete(Long filmId) {
        jdbcTemplate.update(DELETE_SQL, filmId);
    }

    private Director mapDirector(ResultSet row, int rowNum) throws SQLException {
        return Director.builder()
                .id(row.getLong("director_id"))
                .name(row.getString("name"))
                .build();
    }
}
