package ru.yandex.practicum.filmorate.validators;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.Validator;

import javax.validation.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ValidatorTest {
    private final Validator<Film> filmValidator;
    private final Validator<User> userValidator;

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
                .mpa(new Mpa(1,"G"))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.check(f)
        );
        assertEquals("check.t.name: не должно быть пустым", exception.getMessage());
    }

    @Test
    void validateFilmWithDescriptionLongerThan200() {

        Film f  = Film.builder()
                .id(1)
                .name("name")
                .description(LENGTH_201)
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(new Mpa(1,"G"))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.check(f)
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
                .mpa(new Mpa(1,"G"))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.check(f)
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
                .mpa(new Mpa(1,"G"))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmValidator.check(f)
        );
        assertEquals("check.t.duration: должно быть больше 0", exception.getMessage());
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
                () -> userValidator.check(u)
        );
        assertEquals("check.t.email: не должно быть пустым", exception.getMessage());
    }

    @Test
    void validateUserWithEmptyLogin() {
        User u = User.builder()
                .id(1L)
                .email("email@mail.ru")
                .login("")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.check(u)
        );
        assertEquals("check.t.login: Login is empty or contains spaces, check.t.login: Login is empty or contains spaces", exception.getMessage());
    }

    @Test
    void validateUserWithBirthdayAfterNow() {
        User u = User.builder()
                .id(1L)
                .email("email@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().plusYears(20))
                .build();
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidator.check(u)
        );
        assertEquals("check.t.birthday: должно содержать прошедшую дату или сегодняшнее число", exception.getMessage());
    }
}