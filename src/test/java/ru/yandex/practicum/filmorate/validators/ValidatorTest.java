package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    private static final String LENGTH_201 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Test
    void validateFilmWithEmptyName() {

        Film f = new Film(1, "", "desc", LocalDate.now(), 1000L);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Film name is empty", exception.getMessage());
    }

    @Test
    void validateFilmWithDescriptionLongerThan200() {

        Film f = new Film(1, "name", LENGTH_201, LocalDate.now(), 1000L);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Description is longer than 200", exception.getMessage());
    }

    @Test
    void validateFilmWithReleaseDateBefore28Dec1895() {

        Film f = new Film(1, "name", "description", LocalDate.now().minusYears(200), 1000L);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Release date is before 28 december 1895", exception.getMessage());
    }

    @Test
    void validateFilmWithNegativeDuration() {

        Film f = new Film(1, "name", "description", LocalDate.now(), -1L);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Duration is negative", exception.getMessage());
    }

    @Test
    void validateUserWithEmptyEmail() {
        User u = new User(1,"", "login", "name", LocalDate.now().minusYears(20));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(u)
        );
        assertEquals("Invalid email", exception.getMessage());
    }

    @Test
    void validateUserWithEmptyLogin() {
        User u = new User(1,"email", "", "name", LocalDate.now().minusYears(20));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(u)
        );
        assertEquals("Login is empty or contains spaces", exception.getMessage());
    }

    @Test
    void validateUserWithBirthdayAfterNow() {
        User u = new User(1,"email", "login", "name", LocalDate.now().plusYears(20));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(u)
        );
        assertEquals("Birthday is after now", exception.getMessage());
    }
}