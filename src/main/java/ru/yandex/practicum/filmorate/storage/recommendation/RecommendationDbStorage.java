package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component()
@Qualifier("RecommendationDbStorage")
@RequiredArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String GET_RECOMMENDATIONS =
            "SELECT f.* from (SELECT FL3.FILM_ID FROM FILM_LIKE FL1 " +
                    "LEFT JOIN FILM_LIKE FL2 on FL1.FILM_ID = FL2.FILM_ID " +
                    "LEFT JOIN FILM_LIKE FL3 on FL2.USER_ID = FL3.USER_ID " +
                    "WHERE FL1.USER_ID = ? AND FL2.USER_ID = " +
                    "(SELECT FL1.USER_ID FROM FILM_LIKE FL1 " +
                    "LEFT JOIN FILM_LIKE FL2 ON FL1.film_id=FL2.film_id " +
                    "WHERE FL2.USER_ID = ?  AND FL1.USER_ID!= ? " +
                    "GROUP BY FL1.USER_ID " +
                    "ORDER BY COUNT(FL1.FILM_ID) DESC LIMIT 1) " +
                    "AND FL3.FILM_ID != FL1.FILM_ID) fid " +
                    "LEFT JOIN film f ON fid.FILM_ID = f.FILM_ID";

    @Override
    public List<Long> getRecommendations(long id) {
        return jdbcTemplate.query(GET_RECOMMENDATIONS, (rs, i) -> (long) rs.getInt("film_id"), id, id, id);
    }
}
