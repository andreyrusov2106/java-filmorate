package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.event.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static ru.yandex.practicum.filmorate.validators.Validator.validateReview;

@Slf4j
@Service
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewService(@Qualifier("UserDbStorage") UserStorage userStorage,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         ReviewDbStorage reviewDbStorage,
                         FeedStorage feedStorage) {
        this.reviewDbStorage = reviewDbStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
    }

    public Review create(Review review) {
        try {
            validateReview(review);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        if (!userStorage.contains(review.getUserId())) {
            log.warn("User not found" + review.getUserId());
            throw new ResourceNotFoundException("User not found");
        }
        if (!filmStorage.contains(review.getFilmId())) {
            log.warn("User not found" + review.getFilmId());
            throw new ResourceNotFoundException("Film not found");
        }
        Review createdReview = reviewDbStorage.create(review);
        feedStorage.createEvent(createdReview.getUserId(), Operation.ADD, EventType.REVIEW, createdReview.getReviewId());
        log.info("Review created" + review);
        return createdReview;
    }

    public Review update(Review review) {
        try {
            validateReview(review);
        } catch (ValidationException exception) {
            log.warn(exception.getMessage());
            throw exception;
        }
        if (!userStorage.contains(review.getUserId())) {
            log.warn("User not found" + review.getUserId());
            throw new ResourceNotFoundException("User not found");
        }
        if (!filmStorage.contains(review.getFilmId())) {
            log.warn("User not found" + review.getFilmId());
            throw new ResourceNotFoundException("Film not found");
        }
        reviewDbStorage.update(review);
        Review updatedReview = reviewDbStorage.get(review.getReviewId()).
                orElseThrow(() -> new ResourceNotFoundException("review not found"));
        feedStorage.createEvent(updatedReview.getUserId(), Operation.UPDATE, EventType.REVIEW, updatedReview.getReviewId());
        log.info("Review updated" + review);
        return updatedReview;
    }

    public Review get(long id) {
        Optional<Review> review = reviewDbStorage.get(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            log.warn("review not found" + id);
            throw new ResourceNotFoundException("review not found");
        }
    }

    public void delete(long id) {
        Review review = reviewDbStorage.get(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("review with id %s not found", id)));
        reviewDbStorage.delete(id);
        feedStorage.createEvent(review.getUserId(), Operation.REMOVE, EventType.REVIEW, id);
        log.info("Review deleted" + id);
    }

    public List<Review> getAllReviews(long filmId, long count) {
        List<Review> reviews = reviewDbStorage.findAllReviewsByFilmId(filmId, count);
        log.info(String.format("Top %d reviews is %s", count, reviews));
        return reviews.stream().sorted(Comparator.comparingLong(Review::getUseful).reversed()).collect(Collectors.toList());
    }

    public void addLike(long id, long userId) {
        if (!reviewDbStorage.contains(id, userId, true)) {
            if (!reviewDbStorage.contains(id, userId, false)) {
                reviewDbStorage.updateUseful(id, 1);
            } else {
                reviewDbStorage.updateUseful(id, 2);
            }
            reviewDbStorage.addReviewLike(id, userId, true);
        }
    }

    public void addDisLike(long id, long userId) {
        if (!reviewDbStorage.contains(id, userId, false)) {
            if (!reviewDbStorage.contains(id, userId, true)) {
                reviewDbStorage.updateUseful(id, -1);
            } else {
                reviewDbStorage.updateUseful(id, -2);
            }
            reviewDbStorage.addReviewLike(id, userId, false);

        }
    }

    public void removeLike(long id, long userId) {
        if (reviewDbStorage.contains(id, userId, true)) {
            reviewDbStorage.removeReviewLike(id, userId, true);
            reviewDbStorage.updateUseful(id, -1);
        }
    }

    public void removeDisLike(long id, long userId) {
        if (reviewDbStorage.contains(id, userId, false)) {
            reviewDbStorage.removeReviewLike(id, userId, false);
            reviewDbStorage.updateUseful(id, 1);
        }
    }
}
