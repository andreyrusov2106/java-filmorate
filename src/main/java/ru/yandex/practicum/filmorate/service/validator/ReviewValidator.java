package ru.yandex.practicum.filmorate.service.validator;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.ValidationException;

@Service
public class ReviewValidator  implements Validator<Review> {
    @Override
    public void check(Review review) {
        if (review.getUserId() == null) {
            throw new ValidationException("Invalid UserId");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Invalid FilmId");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Invalid IsPositive");
        }
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Invalid Content");
        }
    }
}
