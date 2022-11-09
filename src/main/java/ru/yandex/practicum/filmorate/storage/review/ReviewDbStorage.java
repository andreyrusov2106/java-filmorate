package ru.yandex.practicum.filmorate.storage.review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component()
@Qualifier("ReviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String UPDATE_REVIEW_SQL =
            "UPDATE PUBLIC.REVIEW " +
                    "SET CONTENT=?, IS_POSITIVE=? " +
                    "WHERE REVIEW_ID=?";
    private final static String UPDATE_REVIEW_USEFUL_SQL =
            "UPDATE PUBLIC.REVIEW " +
                    "SET USEFUL=USEFUL+? " +
                    "WHERE REVIEW_ID=?";
    private final static String DELETE_REVIEW_SQL = "DELETE FROM PUBLIC.REVIEW WHERE REVIEW_ID=?";
    private final static String SELECT_REVIEW_BY_ID_SQL = "SELECT * FROM PUBLIC.REVIEW WHERE REVIEW_ID=?";

    private final static String SELECT_TOP_REVIEWS_BY_FILM_ID =
            "SELECT * FROM PUBLIC.REVIEW WHERE FILM_ID=? ORDER BY USEFUL DESC LIMIT ?";
    private final static String SELECT_TOP_REVIEWS =
            "SELECT * FROM PUBLIC.REVIEW ORDER BY USEFUL LIMIT ?";
    private final static String INSERT_REVIEW_LIKE_SQL =
            "MERGE INTO PUBLIC.REVIEW_LIKE (REVIEW_ID, USER_ID, IS_LIKE) VALUES(?,?,?)";
    private final static String DELETE_REVIEW_LIKE_SQL =
            "DELETE FROM PUBLIC.REVIEW_LIKE WHERE REVIEW_ID=? AND USER_ID=? AND IS_LIKE=?";
    private final static String SELECT_REVIEW_LIKE_SQL =
            "SELECT * FROM PUBLIC.REVIEW_LIKE WHERE REVIEW_ID=? AND USER_ID=? AND IS_LIKE=?";

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEW")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_REVIEW_SQL);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getReviewId());
            return ps;
        });
        return review;
    }

    @Override
    public void updateUseful(long id, long likeCount) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(UPDATE_REVIEW_USEFUL_SQL);
            ps.setLong(1, likeCount);
            ps.setLong(2, id);
            return ps;
        });
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_REVIEW_SQL, id);
    }

    @Override
    public Optional<Review> get(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_REVIEW_BY_ID_SQL, this::mapRowToReview, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> findAllReviewsByFilmId(long film_id, long count) {
        if (film_id == -1) {
            return jdbcTemplate.query(SELECT_TOP_REVIEWS, this::mapRowToReview, count);
        } else {
            return jdbcTemplate.query(SELECT_TOP_REVIEWS_BY_FILM_ID, this::mapRowToReview, film_id, count);
        }
    }

    @Override
    public void addReviewLike(long idReview, long idUser, boolean like) {
        jdbcTemplate.update(INSERT_REVIEW_LIKE_SQL,
                idReview,
                idUser,
                like);
    }

    @Override
    public void removeReviewLike(long idReview, long idUser, boolean like) {
        jdbcTemplate.update(DELETE_REVIEW_LIKE_SQL,
                idReview,
                idUser,
                like);
    }

    @Override
    public Boolean contains(long id, long userId, boolean isLike) {
        var rows = jdbcTemplate.queryForRowSet(SELECT_REVIEW_LIKE_SQL, id, userId, isLike);
        return rows.isBeforeFirst();

    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
