package ru.yandex.practicum.filmorate.storage.likes;

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
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_FILM_LIKE_SQL = "INSERT INTO PUBLIC.FILM_LIKE (FILM_ID, USER_ID) VALUES(?,?)";
    private final String DELETE_FILM_LIKE_SQL = "DELETE FROM PUBLIC.FILM_LIKE  WHERE FILM_ID=? AND USER_ID=? ";

    private final String SELECT_FILM_LIKE_SQL = "SELECT USER_ID FROM PUBLIC.FILM_LIKE  WHERE FILM_ID=? ";

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
