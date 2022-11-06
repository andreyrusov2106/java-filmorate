package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component()
@Qualifier("RecommendationDbStorage")
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Override
    public List<Film> getRecommendations(Long id) {
        List<Film> films = new ArrayList<>();
        String RECOMMENDATION_FIND_USER = "select FILM_LIKE.USER_ID, count(film_id) FROM FILM_LIKE WHERE film_id IN (\n" +
                "SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ? AND FILM_LIKE.USER_ID != ?) GROUP BY USER_ID\n" +
                "ORDER BY COUNT(FILM_ID) DESC LIMIT 1";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(RECOMMENDATION_FIND_USER, id, id);
        Integer userNewId = null;
        if (userRows.next()) {
            userNewId = userRows.getInt("user_id");
        }
        if (userNewId != null) {
            String GET_RECOMMENDATIONS = "SELECT * FROM FILM_LIKE WHERE FILM_ID in (SELECT FILM_ID FROM FILM_LIKE\n" +
                    "WHERE USER_ID = ?) AND FILM_ID NOT IN(SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ?)";
            SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet(GET_RECOMMENDATIONS, userNewId, id);
            if (filmIdRows.next()) {
                int filmId = filmIdRows.getInt("film_id");
                films.add(filmDbStorage.getFilm((long) filmId));
            }
        }
        return films;
    }
}
