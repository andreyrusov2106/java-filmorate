package ru.yandex.practicum.filmorate.storage.review;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(long id);

    Optional<Review> get(long id);

    Boolean contains(long id, long userId, boolean isLike);

    List<Review> findAllReviewsByFilmId(long film_id, long count);

    void updateUseful(long id, long likeCount);

    void addReviewLike(long idReview, long idUser, boolean like);

    void removeReviewLike(long idReview, long idUser, boolean like);
}
