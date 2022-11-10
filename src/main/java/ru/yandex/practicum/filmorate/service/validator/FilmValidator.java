package ru.yandex.practicum.filmorate.service.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.Month;

@Service
@Validated
public class FilmValidator implements Validator<Film> {
    @Override
    public void check(@Valid Film film) {

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Description is longer than 200");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Release date is before 28 december 1895");
        }
    }
}
