package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.GenreDao;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GenreDaoImpl implements GenreDao {

    private static final String SELECT_ALL = "select * from GENRES";
    private static final String SELECT_BY_ID = "select * from GENRES where genre_id =  ?";
    private static final String ADD_GENRE = "insert into GENRES (NAME) values (?)";
    private static final String UPDATE_BY_ID = "update GENRES set NAME = ? where genre_id = ?";

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
        return Optional.of(jdbcTemplate.queryForObject(SELECT_BY_ID, Genre.class, id));
    }

    @Override
    public Optional<Genre> create(Genre genre) {
        if (jdbcTemplate.update(ADD_GENRE, genre.getName()) != 1) {
            return Optional.empty();
        } else {
            return Optional.of(genre);
        }
    }

    @Override
    public Optional<Genre> update(Genre genre) {
        if (jdbcTemplate.update(UPDATE_BY_ID, (genre.ordinal()) + 1) != 1) {
            return Optional.empty();
        } else {
            return Optional.of(genre);
        }
    }
}
