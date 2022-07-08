package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.jdbc.DirectorDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("DirectorDaoImpl")
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private static final String SELECT_BY_ID = "SELECT director_id, name FROM directors WHERE director_id=?";
    private static final String SELECT_ALL = "SELECT director_id, name FROM directors";
    private static final String UPDATE = "UPDATE directors SET name=? WHERE director_id=?";
    private static final String DELETE = "DELETE FROM directors WHERE director_id=?";

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("directors").usingGeneratedKeyColumns("director_id");
    }

    @Override
    public Optional<Director> findById(Long directorId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT_BY_ID, this::mapRow, directorId));
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
        try {
            Long director_id = jdbcInsert.executeAndReturnKey(objectToMap(director)).longValue();

            return director_id;
        } catch (EmptyResultDataAccessException e) {
            log.warn(String.format("create director exception for id: %s", director.getId()));
            return null;
        }
    }

    @Override
    public boolean update(Director director) {
        if (jdbcTemplate.update(UPDATE, director.getName(), director.getId()) > 0) {
            return true;
        } else return false;
    }

    @Override
    public boolean delete(Long directorId) {
        if (jdbcTemplate.update(DELETE, directorId) > 0) {
            return true;
        } else return false;
    }

    public Map<String, Object> objectToMap(Director director) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", director.getName());
        return map;
    }

    private Director mapRow(ResultSet row, int rowNum) throws SQLException {
        return Director.builder()
                .id(row.getLong("director_id"))
                .name(row.getString("name"))
                .build();
    }
}