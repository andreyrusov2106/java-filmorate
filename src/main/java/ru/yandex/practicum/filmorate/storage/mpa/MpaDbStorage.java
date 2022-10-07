package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component()
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String SELECT_ALL_GENRES_SQL = "SELECT * FROM PUBLIC.RATING";
    private final String SELECT_GENRE_BY_ID_SQL ="SELECT * FROM PUBLIC.RATING WHERE RATING_ID=?";

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Mpa> getAllMpa() {

        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, this::mapRowToEntity );
    }
    public Mpa getMpa(long idMpa) {
        try {
            return jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID_SQL, this::mapRowToEntity, idMpa);
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    private Mpa mapRowToEntity(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}