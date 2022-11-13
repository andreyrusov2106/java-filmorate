package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FriendDbStorage friendStorage;
    private final MpaDbStorage mpaStorage;
    private final LikeDbStorage likeStorage;
    private final GenreDbStorage genreStorage;
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;

    @Test
    public void testBeans() {
        Assertions.assertThat(filmStorage).isNotNull();
        Assertions.assertThat(userStorage).isNotNull();
        Assertions.assertThat(friendStorage).isNotNull();
        Assertions.assertThat(mpaStorage).isNotNull();
        Assertions.assertThat(likeStorage).isNotNull();
        Assertions.assertThat(genreStorage).isNotNull();
        Assertions.assertThat(reviewStorage).isNotNull();
    }

    //UserTests
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testCreateUser() {
        User u = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser = userStorage.create(u);
        assertThat(createdUser).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testFindUserById() {
        User user1 = userStorage.getUser(1L);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testFindUserByWrongId() {

        final EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class,
                () -> userStorage.getUser(2100L)
        );
        assertEquals("Incorrect result size: expected 1, actual 0", exception.getMessage());

    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testUpdateUser() {
        User u = User.builder()
                .id(1L)
                .email("email2")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User updatesUser = userStorage.update(u);
        assertThat(updatesUser).hasFieldOrPropertyWithValue("email", "email2");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testGetAllUsers() {
        var users = userStorage.findAll();
        assertEquals(3, users.size(), "Неверное количество пользователей.");
    }

    //FilmTests
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testCreateFilm() {
        Film f = Film.builder()
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Film createdFilm = filmStorage.create(f);
        assertThat(createdFilm).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testFindFilmById() {
        Film film1 = filmStorage.getFilm(1L);
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testFindFilmByWrongId() {

        final EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class,
                () -> filmStorage.getFilm(2100L)
        );
        assertEquals("Incorrect result size: expected 1, actual 0", exception.getMessage());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testUpdateFilm() {
        Film f = Film.builder()
                .id(1L)
                .name("name3")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Film updatedFilm = filmStorage.update(f);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "name3");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testGetAllFilms() {
        var films = filmStorage.findAll();
        assertEquals(3, films.size(), "Неверное количество фильмов.");
    }

    //FriendsTests
    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testGetFriendship() {
        var users = friendStorage.getAllFriends(1L);
        assertEquals(2, users.size(), "Неверное количество друзей.");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testGetCommonFriends() {
        var users = friendStorage.getCommonFriends(1L, 3L);
        assertEquals(1, users.size(), "Неверное количество друзей.");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testRemoveFriendship() {
        friendStorage.removeFriend(1L, 3L);
        var users = friendStorage.getAllFriends(3L);
        assertEquals(1, users.size(), "Неверное количество друзей.");
    }

    //MpaTests
    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testGetAllMpa() {
        var mpas = mpaStorage.getAllMpa();
        assertEquals(5, mpas.size(), "Неверное количество Mpa.");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testGetMpa() {
        var mpa = mpaStorage.getMpa(1);
        assertThat(mpa).isPresent().hasValueSatisfying(mpa1 ->
                assertThat(mpa1).hasFieldOrPropertyWithValue("name", "G")
        );
    }

    //LikeTests
    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testAddLike() {
        likeStorage.addFilmLike(1L, 1L);
        var likes = likeStorage.getAllLikes(1L);
        assertEquals(4, likes.size(), "Неверное количество лайков");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void removeLike() {
        likeStorage.removeFilmLike(1L, 1L);
        var likes = likeStorage.getAllLikes(1L);
        assertEquals(2, likes.size(), "Неверное количество лайков");
    }

    //LikeGenres
    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void testAddGenre() {
        var genres = genreStorage.getAllGenres();
        assertEquals(6, genres.size(), "Неверное количество жанров");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void addGenre() {
        genreStorage.addFilmGenre(1L, 1L);
        genreStorage.addFilmGenre(1L, 2L);
        var genres = genreStorage.getFilmGenres(1L);
        assertEquals(4, genres.size(), "Неверное количество жанров");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void removeGenre() {
        genreStorage.addFilmGenre(2L, 1L);
        genreStorage.removeFilmAllGenre(2L);
        var genres = genreStorage.getFilmGenres(2L);
        assertEquals(0, genres.size(), "Неверное количество жанров");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void createReview() {
        Optional<Review> review = reviewStorage.getReviewById(1L);
        assertTrue(review.isPresent());
        assertEquals(5, review.get().getUseful(), "Неверная полезность");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void updateReview() {
        Optional<Review> review = reviewStorage.getReviewById(1L);
        assertTrue(review.isPresent());
        Review r1 = review.get();
        r1.setUseful(100);
        var new_review = reviewStorage.update(r1);
        assertEquals(100, new_review.getUseful(), "Неверная полезность");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
    public void deleteReview() {
        reviewStorage.delete(1L);
        var reviews = reviewStorage.findAllReviewsByFilmId(1L, 10);
        assertEquals(0, reviews.size(), "Неверное количетво ревью");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
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
    @Sql({"/schema.sql","/data.sql", "/test-data.sql"})
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
    @Sql({"/schema.sql", "/data.sql", "/test-data.sql"})
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