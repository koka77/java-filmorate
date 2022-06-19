package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.GenreDao;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenreDaoImpl  implements GenreDao {

    private static final String SELECT_ALL = "select * from GENRES";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.queryForStream(SELECT_ALL,
                (rs, rowNum) -> Genre.valueOf(rs.getString("name"))).collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Genre> add(Genre film) {
        return Optional.empty();
    }

    @Override
    public Optional<Genre> update(Genre film) {
        return Optional.empty();
    }
}
