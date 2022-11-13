package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component()
@Qualifier("MpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String SELECT_ALL_GENRES_SQL = "SELECT * FROM RATING";
    private final static String SELECT_GENRE_BY_ID_SQL = "SELECT * FROM RATING WHERE RATING_ID = ?";

    @Override
    public List<Mpa> getAllMpa() {

        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, this::mapRowToEntity);
    }

    @Override
    public Optional<Mpa> getMpa(long idMpa) {
        try {
            return Optional.of(Objects.requireNonNull(jdbcTemplate.queryForObject(SELECT_GENRE_BY_ID_SQL,
                    this::mapRowToEntity, idMpa)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Mpa mapRowToEntity(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}