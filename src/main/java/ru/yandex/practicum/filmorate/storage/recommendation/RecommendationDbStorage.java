package ru.yandex.practicum.filmorate.storage.recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component()
@Qualifier("RecommendationDbStorage")
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String FIND_USER = "SELECT USER_ID, FILM_ID FROM FILM_LIKE WHERE film_id IN\n" +
            "(SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ?) AND FILM_LIKE.USER_ID != ? GROUP BY USER_ID\n" +
            "ORDER BY COUNT(FILM_ID) DESC LIMIT 1";

    private final static String GET_RECOMMENDATIONS = "SELECT * FROM FILM_LIKE WHERE FILM_ID IN " +
            "(SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ?) AND FILM_ID NOT IN " +
            "(SELECT FILM_ID FROM FILM_LIKE WHERE USER_ID = ?)";

    @Override
    public List<Long> getRecommendations(long id) {
        List<Long> filmsId = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(FIND_USER, id, id);
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
}
