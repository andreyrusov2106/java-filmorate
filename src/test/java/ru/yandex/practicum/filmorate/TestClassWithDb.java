package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestClassWithDb {
    private final FilmService filmService;

    @Test
    public void testBeans() {
        assertThat(filmService).isNotNull();
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-top-n-films.sql"})
    public void testFindTopNFilmsByGenreAndYear() {
        int count = 2;
        int genreId = 1;
        int year = 2001;
        Genre genre = new Genre(1, "Комедия");

        List<Film> films = filmService.findTopFilmsByGenreAndYear(count, genreId, year);

        assertEquals(count, films.size());
        for (Film film : films) {
            assertTrue(film.getGenres().contains(genre));

            int actualYear = film.getReleaseDate().getYear();
            assertEquals(year, actualYear);
        }
    }

    @Test
    @Sql({"/schema.sql","/data.sql", "/test-top-n-films.sql"})
    public void testFindTopNFilmsByGenre() {
        int count = 2;
        int genreId = 1;
        int year = 0;
        Genre genre = new Genre(1, "Комедия");

        List<Film> films = filmService.findTopFilmsByGenreAndYear(count, genreId, year);

        assertEquals(count, films.size());
        for (Film film : films) {
            assertTrue(film.getGenres().contains(genre));
        }
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-top-n-films.sql"})
    public void testFindTopNFilmsByYear() {
        int count = 2;
        int genreId = 0;
        int year = 2001;

        List<Film> films = filmService.findTopFilmsByGenreAndYear(count, genreId, year);

        assertEquals(count, films.size());
        for (Film film : films) {
            int actualYear = film.getReleaseDate().getYear();
            assertEquals(year, actualYear);
        }
    }
}
