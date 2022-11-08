package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component()
@Qualifier("RecommendationDbStorage")
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmService filmService;

    String RECOMMENDATION_FIND_USER = "SELECT USER_ID, FILM_ID FROM FILM_LIKE WHERE film_id IN\n" +
            "(SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ?) AND FILM_LIKE.USER_ID != ? GROUP BY USER_ID\n" +
            "ORDER BY COUNT(FILM_ID) DESC LIMIT 1";

//    String RECOMMENDATION_FIND_USER = "select user_id, count(film_id) FROM LIKES WHERE film_id IN (\n" +
//            "SELECT FILM_ID FROM LIKES WHERE USER_ID = ?) AND LIKES.USER_ID != ? GROUP BY USER_ID\n" +
//            "ORDER BY COUNT(FILM_ID) DESC LIMIT 1";

    String GET_RECOMMENDATIONS = "SELECT * FROM FILM_LIKE WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_LIKE\n" +
            "WHERE USER_ID = ?) AND FILM_ID NOT IN (SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ?)";

//    String GET_RECOMMENDATIONS = "SELECT * FROM LIKES WHERE FILM_ID in (SELECT FILM_ID FROM LIKEs" +
//            " WHERE USER_ID = ?) AND FILM_ID NOT IN(SELECT FILM_ID FROM LIKEs WHERE USER_ID = ?)";

    @Override
    public List<Long> getRecommendations(long id) {
        List<Long> filmsId = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(RECOMMENDATION_FIND_USER, id, id);
        Integer userNewId = null;
        if (userRows.next()) {
            userNewId = userRows.getInt("user_id");
        }
        if (userNewId != null) {
            SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet(GET_RECOMMENDATIONS, userNewId, id);
            if (filmIdRows.next()) {
                int filmId = filmIdRows.getInt("film_id");
                filmsId.add((long) filmId);
            }
        }
        return filmsId;
    }



//    @Override
//    public List<Film> getRecommendations(int id) {
//        List<Film> films = new ArrayList<>();
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet(RECOMMENDATION_FIND_USER, id, id);
//        Integer userNewId = null;
//        if (userRows.next()) {
//            userNewId = userRows.getInt("user_id");
//        }
//        if (userNewId != null) {
//            SqlRowSet filmIdRows = jdbcTemplate.queryForRowSet(GET_RECOMMENDATIONS, userNewId, id);
//            if (filmIdRows.next()) {
//                int filmId = filmIdRows.getInt("film_id");
//                films.add(filmDbStorage.get(filmId));
//            }
//        }
//        return films;
//    }

}
