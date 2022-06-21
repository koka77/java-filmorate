package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.sql.Date;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDaoImpl")
public class FilmDaoImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;


    private static final String SELECT_ALL = "select * from films order by FILM_ID";
    private static final String SELECT_BY_ID = "select * from films where film_id = ?";

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, FilmGenreDao filmGenreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreDao = filmGenreDao;
    }

    @Override
    public Collection<Film> findAll() {

        return jdbcTemplate.queryForStream(SELECT_ALL,
                (rs, rowNum) ->
                        new Film(
                                rs.getLong("film_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDate("release_date").toLocalDate(),
                                Duration.ofMinutes(rs.getLong("duration"))
                        )
        ).peek(film -> film.setGenres(jdbcTemplate.queryForStream("select * from  GENRES g join FILMS_GENRES fg on g.GENRE_ID = fg.GENRE_ID where film_id = ?",
                                (rs, rowNum) ->
                                        Genre.valueOf(
                                                rs.getString("name")
                                        ), film.getId()
                        ).collect(Collectors.toSet())
                )
        ).peek(film -> film.getLikes().addAll(jdbcTemplate.queryForStream("select * from  LIKES where film_id = ?",
                        (rs2, rowNum2) ->
                                rs2.getLong("user_id")
                        , film.getId()
                ).collect(Collectors.toSet())
        )).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> findById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);

        if (rowSet.next()) {
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
        int countUpdate = jdbcTemplate.update("insert into FILMS (name, description, release_date, duration, MPAA_ID) values ( ?,?,?,?,? )",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getRating() != null ? film.getRating().ordinal() : null);

        if (countUpdate != 1) {
            return Optional.empty();
        }


            filmGenreDao.updateAllGenreByFilm(film);


        /* TODO
        не забыть удалить комментарий если все заработает
         */
       /* for (Enum<Genre> genreEnum : film.getGenres()) {
            countUpdate = jdbcTemplate.update("insert into FILMS_GENRES (FILM_ID, GENRE_ID) values ( ?,? )",
                    film.getId(), genreEnum.name());
        }
        if (countUpdate != film.getGenres().size()) {
            return Optional.empty();
        }
        return Optional.of(film);*/
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        return null;
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        return null;
    }

    private void fillFilmGenreTable(Film film) {
        String sql = "";
        for (Enum<Genre> genre : film.getGenres()) {

        }
    }
}
