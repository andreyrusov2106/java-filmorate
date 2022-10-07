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
        Film f = Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Film name is empty", exception.getMessage());
    }

    @Test
    void validateFilmWithDescriptionLongerThan200() {

        Film f  = Film.builder()
                .id(1)
                .name("name")
                .description(LENGTH_201)
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Description is longer than 200", exception.getMessage());
    }

    @Test
    void validateFilmWithReleaseDateBefore28Dec1895() {

        Film f  = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now().minusYears(200))
                .duration(1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Release date is before 28 december 1895", exception.getMessage());
    }

    @Test
    void validateFilmWithNegativeDuration() {

        Film f  = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(-1000L)
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateFilm(f)
        );
        assertEquals("Duration is negative", exception.getMessage());
    }

    @Test
    void validateUserWithEmptyEmail() {
        User u = User.builder()
                .id(1L)
                .email("")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(u)
        );
        assertEquals("Invalid email", exception.getMessage());
    }

    @Test
    void validateUserWithEmptyLogin() {
        User u = User.builder()
                .id(1L)
                .email("email")
                .login("")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(u)
        );
        assertEquals("Login is empty or contains spaces", exception.getMessage());
    }

    @Test
    void validateUserWithBirthdayAfterNow() {
        User u = User.builder()
                .id(1L)
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().plusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validator.validateUser(u)
        );
        assertEquals("Birthday is after now", exception.getMessage());
    }
}