package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorFilmDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component()
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorFilmDbStorage directorFilmDbStorage;

    private final static String UPDATE_FILM_SQL =
            "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                    "WHERE FILM_ID = ?";
    private final static String SELECT_FILM_BY_ID_SQL =
            "SELECT F.*, R.NAME AS RATING_NAME " +
                    "FROM FILM F LEFT JOIN RATING R ON F.RATING_ID = R.RATING_ID " +
                    "WHERE F.FILM_ID = ?";
    private final static String SELECT_ALL_FILM_SQL =
            "SELECT F.*, r.NAME AS RATING_NAME " +
                    "FROM PUBLIC.FILM f LEFT JOIN PUBLIC.RATING r ON F.RATING_ID = R.RATING_ID";

    private final static String SELECT_TOP10_FILMS =
            "SELECT F.*, R.NAME AS RATING_NAME, COUNT(FL.USER_ID) LIKES " +
                    "FROM PUBLIC.FILM F LEFT JOIN PUBLIC.FILM_LIKE FL ON F.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN PUBLIC.RATING R ON F.RATING_ID = R.RATING_ID " +
                    "GROUP BY F.FILM_ID ORDER BY LIKES DESC LIMIT ?";

    private final static String TOP_N_FILMS_BY_GENRE_AND_YEAR =
            "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, " +
                    "F.RELEASE_DATE, F.DURATION, R.RATING_ID, R.NAME AS rating_name FROM FILM AS F " +
                    "JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                    "WHERE F.FILM_ID IN (" +
                    "SELECT F.FILM_ID " +
                    "FROM FILM AS F " +
                    "LEFT JOIN FILM_LIKE FL ON F.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN FILM_GENRE FG on F.FILM_ID = FG.FILM_ID " +
                    "LEFT JOIN GENRE AS G ON FG.GENRE_ID = G.GENRE_ID " +
                    "WHERE G.GENRE_ID = ? AND EXTRACT(YEAR FROM F.RELEASE_DATE) = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(DISTINCT FL.USER_ID) DESC " +
                    "LIMIT ?)";

    private final static String TOP_N_FILMS_BY_GENRE =
            "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, " +
                    "R.RATING_ID, R.NAME AS rating_name " +
                    "FROM FILM AS F " +
                    "JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                    "WHERE F.FILM_ID IN (" +
                    "SELECT F.FILM_ID " +
                    "FROM FILM AS F " +
                    "LEFT JOIN FILM_LIKE FL ON F.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
                    "LEFT JOIN GENRE AS G ON FG.GENRE_ID = G.GENRE_ID " +
                    "WHERE G.GENRE_ID = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(DISTINCT FL.USER_ID) DESC " +
                    "LIMIT ?)";

    private final static String TOP_N_FILMS_BY_YEAR =
            "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, " +
                    "F.RELEASE_DATE, F.DURATION, R.RATING_ID, R.NAME AS rating_name " +
                    "FROM FILM AS F " +
                    "JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                    "WHERE F.FILM_ID IN (" +
                    "SELECT F.FILM_ID FROM FILM AS F " +
                    "LEFT JOIN FILM_LIKE FL ON F.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID " +
                    "LEFT JOIN GENRE AS G ON FG.GENRE_ID = G.GENRE_ID " +
                    "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE) = ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(DISTINCT FL.USER_ID) DESC " +
                    "LIMIT ?)";

    private final static String COMMON_FILMS =
            "SELECT * FROM (SELECT fli.FILM_ID, " +
                    "COUNT(fli.USER_ID) rate, " +
                    "RATING.NAME as rating_name FROM (SELECT FILM_ID " +
                    "FROM FILM_LIKE " +
                    "WHERE USER_ID = ? " +
                    "INTERSECT SELECT distinct FILM_ID " +
                    "FROM FILM_LIKE " +
                    "WHERE USER_ID = ?) as a " +
                    "LEFT JOIN FILM_LIKE fli ON (fli.FILM_ID = a.FILM_ID) " +
                    "JOIN FILM  ON (FILM.FILM_ID = a.FILM_ID) " +
                    "JOIN RATING  ON RATING.RATING_ID = FILM.RATING_ID " +
                    "GROUP BY fli.FILM_ID " +
                    "ORDER BY rate DESC) fid " +
                    "LEFT JOIN film f on fid.FILM_ID = f.FILM_ID";

    private final static String SELECT_SORT_YEAR_SQL =
            "SELECT df.film_id " +
                    "FROM director_films df " +
                    "JOIN film f ON df.film_id = f.film_id " +
                    "WHERE df.director_id=? ORDER BY f.release_date";

    private final static String SELECT_SORT_LIKE_SQL =
            "SELECT df.film_id " +
                    "FROM director_films df " +
                    "LEFT JOIN film_like l ON df.film_id = l.film_id " +
                    "WHERE df.director_id=? " +
                    "GROUP BY df.film_id, l.user_id ORDER BY COUNT(l.user_id) DESC";

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        directorFilmDbStorage.refresh(film);
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
        directorFilmDbStorage.refresh(film);
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
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Boolean contains(Long id) {
        try {
            jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_SQL, this::mapRowToFilm, id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public Film getFilm(Long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FILM_BY_ID_SQL, this::mapRowToFilm, id);
        } catch(EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Film with id=" + id + " not found");
        }
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

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return jdbcTemplate.query(COMMON_FILMS, this::mapRowToFilm, userId, friendId);
    }

    public List<Film> getByDirector(Long directorId, String sortBy) {
        if (sortBy.equals("year")) {
            return jdbcTemplate.query(SELECT_SORT_YEAR_SQL, this::mapFilm, directorId);
        } else {
            return jdbcTemplate.query(SELECT_SORT_LIKE_SQL, this::mapFilm, directorId);
        }
    }

    private Film mapFilm(ResultSet row, int rowNum) throws SQLException {
        return getFilm(row.getLong("film_id"));
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
                .directors(directorFilmDbStorage.getByFilm(resultSet.getLong("film_id")))
                .build();
    }

    @Override
    public boolean removeFilm(Long id) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        return this.jdbcTemplate.update(sqlQuery, id) != 0;
    }

    @Override
    public List<Film> getByTitleSubstring(String substring) {
        String sqlQuery = "SELECT F.*, R.name AS rating_name FROM film F " +
                "LEFT JOIN rating R ON F.rating_id = R.rating_id " +
                "LEFT JOIN film_like FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE LOWER(F.name) LIKE ? " +
                "GROUP BY F.FILM_ID ORDER BY COUNT(FL.user_id) DESC";
        return this.jdbcTemplate.query(sqlQuery, this::mapFilm, '%' + substring + '%');
    }

    @Override
    public List<Film> getByDirectorSubstring(String substring) {
        String sqlQuery = "SELECT F.*, R.name AS rating_name FROM film F " +
                "LEFT JOIN rating R ON F.rating_id = R.rating_id " +
                "LEFT JOIN film_like FL ON F.FILM_ID = FL.FILM_ID " +
                "LEFT JOIN director_films DF ON F.film_id = DF.film_id " +
                "LEFT JOIN directors D ON DF.director_id = D.director_id " +
                "WHERE LOWER(D.name) LIKE ? " +
                "GROUP BY F.FILM_ID ORDER BY COUNT(FL.user_id) DESC";
        return this.jdbcTemplate.query(sqlQuery, this::mapFilm, '%' + substring + '%');
    }

    @Override
    public List<Film> getByDirectorOrTitleSubstring(String substring) {
        String sqlQuery = "SELECT F.*, R.name AS rating_name FROM film F " +
                "LEFT JOIN rating R ON F.rating_id = R.rating_id " +
                "LEFT JOIN film_like FL ON F.FILM_ID = FL.FILM_ID " +
                "LEFT JOIN director_films DF ON F.film_id = DF.film_id " +
                "LEFT JOIN directors D ON DF.director_id = D.director_id " +
                "WHERE LOWER(D.name) LIKE ? OR LOWER(F.name) LIKE ? " +
                "GROUP BY F.FILM_ID ORDER BY COUNT(FL.user_id) DESC";
        substring += '%';
        substring = '%' + substring;
        return this.jdbcTemplate.query(sqlQuery, this::mapFilm, substring, substring);
    }
}
