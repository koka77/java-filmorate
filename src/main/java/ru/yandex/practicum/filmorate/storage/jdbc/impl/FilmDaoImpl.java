package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpaa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

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
    private static final String SELECT_BY_ID = "select f.FILM_ID as FILM_ID, f.NAME , f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPAA_ID, fg.GENRE_ID, G2.NAME as GNAME, L.USER_ID as `LIKE` from films f  left join  FILMS_GENRES fg " +
            "on f.FILM_ID = fg.FILM_ID left join  GENRES as G2 on fg.GENRE_ID = G2.GENRE_ID left join LIKES L " +
            "on f.FILM_ID = L.FILM_ID\n" +
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
                    (rs.getLong("duration")),
                    new Mpaa(rs.getInt("MPAA_ID"), rs.getString("MPAA_NAME"))
            );
            films.add(film);
        }
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        Film film = null;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_BY_ID, id);
        if (rowSet.next()) {
            Set<Genre> genres = new HashSet<>();
            Set<Long> likes = new HashSet<>();

            film = Film.builder()
                    .description(rowSet.getString("description"))
                    .id(rowSet.getLong("film_id"))
                    .name(rowSet.getString("NAME"))
                    .releaseDate((rowSet.getDate("release_date").toLocalDate()))
                    .duration(rowSet.getLong("duration"))
                    .build();
            do {
                genres.add(Genre.valueOf(rowSet.getString("GNAME")));
                Long l;
                if ((l = rowSet.getLong("LIKE")) != 0L) {
                    likes.add(l);
                }
            } while (rowSet.next());
            film.setGenres(genres);
            film.getLikes().addAll(likes);
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

        updateLikes(film);

        return Optional.of(film);
    }

    public Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("MPAA_ID", film.getMpa());

        return values;
    }


    private void updateLikes(Film film) {
        deleteLikes(film);
        insertLikes(film);
    }

    private void insertLikes(Film film) {
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
