package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component("FilmDaoImpl")
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;


    private static final String SELECT_ALL = "select * from films";
    private static final String SELECT_BY_ID = "select * from films where film_id = ?";

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_ALL);
        return null;
    }

    @Override
    public Optional<Film> findById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);

        if (rowSet.next()){
            Film film = Film.builder()
                    .description(rowSet.getString("description"))
                    .id(rowSet.getLong("film_id"))
                    .name(rowSet.getString("NAME"))
                    .releaseDate((rowSet.getDate("release_date").toLocalDate()))
                    .duration(Duration.ofMinutes(rowSet.getLong("duration")))
                    .build();

            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        return null;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        return null;
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        return null;
    }
}
