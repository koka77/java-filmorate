package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDaoImpl")
public class FilmDaoImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;


    private static final String SELECT_ALL = "select FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE," +
            " FILMS.DURATION, FILMS.MPAA_ID, MPAA.NAME as MPAA_NAME from films " +
            "join MPAA on FILMS.MPAA_ID = MPAA.MPAA_ID order by FILM_ID";
    private static final String SELECT_BY_ID = "select f.FILM_ID as FILM_ID, f.NAME , f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPAA_ID, M2.NAME as MPAA_NAME, fg.GENRE_ID as GID, G2.NAME as GNAME, L.USER_ID as `LIKE` from films f  left join  FILMS_GENRES fg " +
            "on f.FILM_ID = fg.FILM_ID left join  GENRES as G2 on fg.GENRE_ID = G2.GENRE_ID left join LIKES L " +
            "on f.FILM_ID = L.FILM_ID " +
            "left join MPAA M2 on f.MPAA_ID = M2.MPAA_ID " +
            "where f.FILM_ID = ?";

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, FilmGenreDao filmGenreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreDao = filmGenreDao;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_ALL);
        while (rs.next()) {
            Film film = new Film(
                    rs.getLong("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    (rs.getInt("duration")),
                    new Mpa(rs.getInt("MPAA_ID"), rs.getString("MPAA_NAME"))
            );
            films.add(film);
        }
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        Film film = null;
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);
        if (rs.next()) {
            Set<Genre> genres = new HashSet<>();
            Set<Long> likes = new HashSet<>();

            film = Film.builder()
                    .description(rs.getString("description"))
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("NAME"))
                    .releaseDate((rs.getDate("release_date").toLocalDate()))
                    .duration(rs.getInt("duration"))
                    .build();

            film.setMpa(new Mpa(rs.getInt("MPAA_ID"),
                    rs.getString("MPAA_NAME")));

            do {
                if (rs.getString("GNAME") != null) {
                    genres.add(new Genre(rs.getInt("GID"), rs.getString("GNAME")));
                }
                Long l;
                if ((l = rs.getLong("LIKE")) != 0L) {
                    likes.add(l);
                }
            } while (rs.next());
            film.setGenres(genres);
            film.getLikes().addAll(likes);
            getLikesByFilm(film);
        }
        return Optional.of(film);
    }

    @Override
    public Optional<Film> create(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        film.setId(simpleJdbcInsert.executeAndReturnKey(this.filmToMap(film)).longValue());

        filmGenreDao.updateAllGenreByFilm(film);

        insertLikes(film);

        return Optional.of(film);
    }

    public Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        if (film.getMpa() != null) {
            values.put("MPAA_ID", film.getMpa().getId());
        }

        return values;
    }

    private void getLikesByFilm(Film film) {
        final String sql = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getLong("USER_ID"), film.getId());
        film.getLikes().addAll(likes);
    }

    private void updateLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            deleteLikes(film);
            return;
        }
        deleteLikes(film);
        insertLikes(film);
    }

    private void insertLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            return;
        }
        String sql = "insert into LIKES(USER_ID, FILM_ID) values (?, ?)";

        try (PreparedStatement ps = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql)) {
            for (Long like : film.getLikes()) {
                ps.setLong(1, like);
                ps.setLong(2, film.getId());
                ps.addBatch();
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteLikes(Film film) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());

    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (film.getId() != null && film.getId() < 1) {
            throw new UnableToFindException();
        }
        final String sql = "update FILMS set NAME = ?, DESCRIPTION = ?,RELEASE_DATE = ?, DURATION = ?, MPAA_ID = ?   where FILM_ID = ?";
        int count = jdbcTemplate.update(sql
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());

        if (count == 1) {
            filmGenreDao.updateAllGenreByFilm(film);
            updateLikes(film);
        }
        return Optional.of(film);
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        String sql = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPAA_ID, F.NAME as MPAA_NAME " +
                "from FILMS  F LEFT JOIN  LIKES L on F.FILM_ID  = L.FILM_ID " +
                "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) ->
                {
                    Film film = new Film(
                            rs.getLong("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            (rs.getInt("duration")),
                            new Mpa(rs.getInt("MPAA_ID"), rs.getString("MPAA_NAME")
                            )
                    );
                    getLikesByFilm(film);
                    return film;
                }, count
        );
        return films;
    }
}
