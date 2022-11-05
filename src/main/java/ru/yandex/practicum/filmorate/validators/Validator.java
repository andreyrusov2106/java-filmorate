package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

public class Validator {
    public static void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Film name is empty");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Description is longer than 200");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Release date is before 28 december 1895");
        }
        if (film.getDuration() == null || film.getDuration() < 0) {
            throw new ValidationException("Duration is negative");
        }
    }

    public static void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() && !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login is empty or contains spaces");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday is after now");
        }
    }
    public static void validateReview(Review review) {

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
