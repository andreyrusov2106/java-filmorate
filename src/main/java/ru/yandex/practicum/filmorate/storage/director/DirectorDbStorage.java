package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component()
@Qualifier("DirectorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private final static String SELECT_BY_ID = "SELECT director_id, name FROM directors WHERE director_id = ?";
    private final static String SELECT_ALL = "SELECT director_id, name FROM directors";
    private final static String UPDATE = "UPDATE directors SET name = ? WHERE director_id = ?";
    private final static String DELETE = "DELETE FROM directors WHERE director_id = ?";

    @Override
    public Optional<Director> findById(Long directorId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_ID, this::mapRow, directorId));
        } catch (EmptyResultDataAccessException e) {
            log.warn(String.format("findById exception for id: %s", directorId));
            return Optional.empty();
        }
    }

    @Override
    public Collection<Director> findAll() {
        return jdbcTemplate.queryForStream(SELECT_ALL, this::mapRow).collect(Collectors.toList());
    }

    @Override
    public Long insert(Director director) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        try {
            return jdbcInsert.executeAndReturnKey(objectToMap(director)).longValue();
        } catch (EmptyResultDataAccessException e) {
            log.warn(String.format("create director exception for id: %s", director.getId()));
            return null;
        }
    }

    @Override
    public boolean update(Director director) {
        return jdbcTemplate.update(UPDATE, director.getName(), director.getId()) > 0;
    }

    @Override
    public boolean delete(Long directorId) {
        return jdbcTemplate.update(DELETE, directorId) > 0;
    }

    private Director mapRow(ResultSet row, int rowNum) throws SQLException {
        return Director.builder()
                .id(row.getLong("director_id"))
                .name(row.getString("name"))
                .build();
    }

    public Map<String, Object> objectToMap(Director director) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", director.getName());
        return map;
    }
}
