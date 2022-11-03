package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase
class FilmorateApplicationTest {
    private static UserDbStorage userStorage;
    private static FilmDbStorage filmStorage;
    private static FriendDbStorage friendStorage;
    private static MpaDbStorage mpaStorage;
    private static LikeDbStorage likeStorage;
    private static GenreDbStorage genreStorage;


    @BeforeAll
    public static void setUp(@Autowired UserDbStorage userStorage,
                             @Autowired FilmDbStorage filmDbStorage,
                             @Autowired FriendDbStorage friendStorage,
                             @Autowired MpaDbStorage mpaStorage,
                             @Autowired LikeDbStorage likeStorage,
                             @Autowired GenreDbStorage genreStorage
                             ){
        setUserStorage(userStorage);
        setFilmStorage(filmDbStorage);
        setFriendStorageStorage(friendStorage);
        setLikeStorage(likeStorage);
        setMpaStorage(mpaStorage);
        setGenreStorage(genreStorage);
        User u = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser=userStorage.create(u);
        Film f  = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmDbStorage.create(f);
        User u2 = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser2=userStorage.create(u2);
        User u3 = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser3=userStorage.create(u3);
        friendStorage.addFriend(createdUser.getId(),createdUser2.getId());
        friendStorage.addFriend(createdUser3.getId(),createdUser2.getId());
        friendStorage.addFriend(createdUser3.getId(),createdUser.getId());


    }

    public static void setUserStorage(UserDbStorage userStorage) {
        FilmorateApplicationTest.userStorage = userStorage;

    }
    public static void setFilmStorage(FilmDbStorage filmDbStorage) {
        FilmorateApplicationTest.filmStorage = filmDbStorage;

    }
    public static void setFriendStorageStorage(FriendDbStorage friendStorage) {
        FilmorateApplicationTest.friendStorage = friendStorage;

    }

    public static void setMpaStorage(MpaDbStorage mpaStorage) {
        FilmorateApplicationTest.mpaStorage = mpaStorage;
    }

    public static void setLikeStorage(LikeDbStorage likeStorage) {
        FilmorateApplicationTest.likeStorage = likeStorage;
    }

    public static void setGenreStorage(GenreDbStorage genreStorage) {
        FilmorateApplicationTest.genreStorage = genreStorage;
    }

    //UserTests
    @Test
    public void testCreateUser() {
        User u = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User createdUser=userStorage.create(u);
        assertThat(createdUser).hasFieldOrPropertyWithValue("id", 4L);
    }
    @Test
    public void testFindUserById() {

        User  user1 = userStorage.getUser(1L);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);

    }

    @Test
    public void testFindUserByWrongId() {

        final EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class,
                () -> userStorage.getUser(2100L)
        );
        assertEquals("Incorrect result size: expected 1, actual 0", exception.getMessage());

    }
    @Test
    public void testUpdateUser() {
        User u = User.builder()
                .id(1L)
                .email("email2")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        User updatesUser=userStorage.update(u);
        assertThat(updatesUser).hasFieldOrPropertyWithValue("email", "email2");
    }

    @Test
    public void testGetAllUsers() {
        var users=userStorage.findAll();
        assertEquals(3, users.size(), "Неверное количество пользователей.");
    }
    //FilmTests
    @Test
    public void testCreateFilm() {
        Film f  = Film.builder()
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Film createdFilm= filmStorage.create(f);
        assertThat(createdFilm).hasFieldOrPropertyWithValue("id", 2L);
    }
    @Test
    public void testFindFilmById() {
        Film film1= filmStorage.getFilm(1L);
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testFindFilmByWrongId() {

        final EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class,
                () -> filmStorage.getFilm(2100L)
        );
        assertEquals("Incorrect result size: expected 1, actual 0", exception.getMessage());
    }

    @Test
    public void testUpdateFilm() {
        Film f  = Film.builder()
                .id(1L)
                .name("name3")
                .description("description2")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Film updatedFilm= filmStorage.update(f);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "name3");
    }
    @Test
    public void testGetAllFilms() {
        var films=filmStorage.findAll();
        assertEquals(1, films.size(), "Неверное количество фильмов.");
    }
    //FriendsTests
    @Test
    public void testGetFriendship() {
        var users=friendStorage.getAllFriends(1L);
        assertEquals(1, users.size(), "Неверное количество друзей.");
    }
    @Test
    public void testGetCommonFriends() {
        var users=friendStorage.getCommonFriends(1L,3L);
        assertEquals(1, users.size(), "Неверное количество друзей.");
    }
    @Test
    public void testRemoveFriendship() {
        friendStorage.removeFriend(3L,1L);
        var users=friendStorage.getAllFriends(3L);
        assertEquals(1, users.size(), "Неверное количество друзей.");
    }
    //MpaTests
    @Test
    public void testGetAllMpa() {
        var mpas=mpaStorage.getAllMpa();
        assertEquals(5, mpas.size(), "Неверное количество Mpa.");
    }
    @Test
    public void testGetMpa() {
        var mpa=mpaStorage.getMpa(1);
        assertThat(mpa).isPresent().hasValueSatisfying(mpa1 ->
                assertThat(mpa1).hasFieldOrPropertyWithValue("name", "G")
        );
    }
    //LikeTests
    @Test
    public void testAddLike() {
        likeStorage.addFilmLike(1L,1L);
        var likes=likeStorage.getAllLikes(1L);
        assertEquals(1, likes.size(), "Неверное количество лайков");
    }
    @Test
    public void removeLike() {
        likeStorage.removeFilmLike(1L,1L);
        var likes=likeStorage.getAllLikes(1L);
        assertEquals(0, likes.size(), "Неверное количество лайков");
    }
    //LikeGenres
    @Test
    public void testAddGenre() {
        var genres=genreStorage.getAllGenres();
        assertEquals(6, genres.size(), "Неверное количество жанров");
    }
    @Test
    public void addGenre() {
        genreStorage.addFilmGenre(1L,1L);
        genreStorage.addFilmGenre(1L,2L);
        var genres=genreStorage.getFilmGenres(1L);
        assertEquals(2, genres.size(), "Неверное количество жанров");
    }

    @Test
    public void removeGenre() {
        genreStorage.addFilmGenre(2L,1L);
        genreStorage.removeFilmAllGenre(2L);
        var genres=genreStorage.getFilmGenres(2L);
        assertEquals(0, genres.size(), "Неверное количество жанров");
    }

}