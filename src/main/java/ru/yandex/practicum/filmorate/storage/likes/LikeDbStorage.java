package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component()
@Qualifier("LikeDbStorage")
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String INSERT_FILM_LIKE_SQL = "INSERT INTO FILM_LIKE (FILM_ID, USER_ID) VALUES(?, ?)";
    private final static String DELETE_FILM_LIKE_SQL = "DELETE FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID = ?";
    private final static String SELECT_FILM_LIKE_SQL = "SELECT USER_ID FROM FILM_LIKE WHERE FILM_ID = ?";

    @Override
    public void addFilmLike(Long idFilm, Long idUser) {
        jdbcTemplate.update(INSERT_FILM_LIKE_SQL,
                idFilm,
                idUser);
    }

    @Override
    public void removeFilmLike(Long idFilm, Long idUser) {
        jdbcTemplate.update(DELETE_FILM_LIKE_SQL,
                idFilm,
                idUser);
    }

    @Override
    public List<Long> getAllLikes(Long idFilm) {
        try {
            return jdbcTemplate.queryForList(SELECT_FILM_LIKE_SQL, Long.class, idFilm);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

}
