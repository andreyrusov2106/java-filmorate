package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component()
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String UPDATE_FILM_SQL =
            "UPDATE PUBLIC.FILM " +
                    "SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? , RATING_ID=? " +
                    "WHERE FILM_ID=?";
    private static final String SELECT_FILM_BY_ID_SQL =
            "SELECT F.*, R.NAME AS RATING_NAME " +
                    "FROM PUBLIC.FILM F LEFT JOIN PUBLIC.RATING R ON F.RATING_ID=R.RATING_ID " +
                    "WHERE F.FILM_ID=?";
    private static final String SELECT_ALL_FILM_SQL =
            "SELECT F.*, r.NAME AS RATING_NAME " +
                    "FROM PUBLIC.FILM f LEFT JOIN PUBLIC.RATING r ON F.RATING_ID=R.RATING_ID";

    private static final String SELECT_TOP10_FILMS =
            "SELECT F.*, R.NAME AS RATING_NAME, COUNT(FL.USER_ID) LIKES " +
                    "FROM PUBLIC.FILM F LEFT JOIN PUBLIC.FILM_LIKE FL ON F.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN PUBLIC.RATING R ON F.RATING_ID=R.RATING_ID " +
                    "GROUP BY F.NAME ORDER BY LIKES DESC LIMIT ?";

    private static final String TOP_N_FILMS_BY_GENRE_AND_YEAR = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION,\n" +
            "       R.RATING_ID, R.NAME as rating_name\n" +
            "from FILM as F\n" +
            "         join RATING as R on F.RATING_ID = R.RATING_ID\n" +
            "where F.FILM_ID in (\n" +
            "    select FL.FILM_ID\n" +
            "    from FILM as F\n" +
            "             join FILM_LIKE FL on F.FILM_ID = FL.FILM_ID\n" +
            "             join FILM_GENRE FG on F.FILM_ID = FG.FILM_ID\n" +
            "             join GENRE as G on FG.GENRE_ID = G.GENRE_ID\n" +
            "    where G.GENRE_ID = ? AND EXTRACT(YEAR from F.RELEASE_DATE) = ?\n" +
            "    group by FL.FILM_ID\n" +
            "    order by COUNT(DISTINCT FL.USER_ID) desc\n" +
            "    limit ?\n" +
            "    )";

    private static final String TOP_N_FILMS_BY_GENRE = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION,\n" +
            "       R.RATING_ID, R.NAME as rating_name\n" +
            "from FILM as F\n" +
            "         join RATING as R on F.RATING_ID = R.RATING_ID\n" +
            "where F.FILM_ID in (\n" +
            "    select FL.FILM_ID\n" +
            "    from FILM as F\n" +
            "             join FILM_LIKE FL on F.FILM_ID = FL.FILM_ID\n" +
            "             join FILM_GENRE FG on F.FILM_ID = FG.FILM_ID\n" +
            "             join GENRE as G on FG.GENRE_ID = G.GENRE_ID\n" +
            "    where G.GENRE_ID = ? \n" +
            "    group by FL.FILM_ID\n" +
            "    order by COUNT(DISTINCT FL.USER_ID) desc\n" +
            "    limit ?\n" +
            "    )";

    private static final String TOP_N_FILMS_BY_YEAR = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION,\n" +
            "       R.RATING_ID, R.NAME as rating_name\n" +
            "from FILM as F\n" +
            "         join RATING as R on F.RATING_ID = R.RATING_ID\n" +
            "where F.FILM_ID in (\n" +
            "    select FL.FILM_ID\n" +
            "    from FILM as F\n" +
            "             join FILM_LIKE FL on F.FILM_ID = FL.FILM_ID\n" +
            "             join FILM_GENRE FG on F.FILM_ID = FG.FILM_ID\n" +
            "             join GENRE as G on FG.GENRE_ID = G.GENRE_ID\n" +
            "    where EXTRACT(YEAR from F.RELEASE_DATE) = ?\n" +
            "    group by FL.FILM_ID\n" +
            "    order by COUNT(DISTINCT FL.USER_ID) desc\n" +
            "    limit ?\n" +
            "    )";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_FILM_SQL);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            ps.setLong(6, film.getId());
            return ps;
        });
        return film;
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(SELECT_ALL_FILM_SQL, this::mapRowToFilm);
    }

    public List<Film> findTop10Films(int count) {
        return jdbcTemplate.query(SELECT_TOP10_FILMS, this::mapRowToFilm, count);
    }

    @Override
    public Boolean contains(Film film) {
        try {
            jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_SQL, this::mapRowToFilm, film.getId());
            return true;
        } catch (
                EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Boolean contains(Long id) {
        try {
            jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_SQL, this::mapRowToFilm, id);
            return true;
        } catch (
                EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Film getFilm(Long id) {
        return jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_SQL, this::mapRowToFilm, id);
    }

    @Override
    public List<Film> findTopFilmsByGenreAndYear(int count, int genreId, int year) {
        if (genreId == 0) {
            return jdbcTemplate.query(TOP_N_FILMS_BY_YEAR, this::mapRowToFilm, year, count);
        }
        if (year == 0) {
            return jdbcTemplate.query(TOP_N_FILMS_BY_GENRE, this::mapRowToFilm, genreId, count);
        }
        return jdbcTemplate.query(TOP_N_FILMS_BY_GENRE_AND_YEAR, this::mapRowToFilm, genreId, year, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Date tmpDate = resultSet.getDate("release_date");
        LocalDate release_date = tmpDate == null ? null : tmpDate.toLocalDate();
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(release_date)
                .duration(resultSet.getLong("duration"))
                .mpa(new Mpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name")))
                .build();
    }
}
