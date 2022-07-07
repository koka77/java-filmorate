package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.DirectorFilmsDao;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDaoImpl")
public class FilmDaoImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private final DirectorFilmsDao directorFilmsDao;


    private static final String SELECT_ALL = "select FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE," +
            " FILMS.DURATION, FILMS.MPAA_ID, MPAA.NAME as MPAA_NAME from films " +
            "join MPAA on FILMS.MPAA_ID = MPAA.MPAA_ID order by FILM_ID";
    private static final String SELECT_BY_ID = "select f.FILM_ID as FILM_ID, " +
            "f.NAME , f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPAA_ID, " +
            "M2.NAME as MPAA_NAME, fg.GENRE_ID as GID, " +
            "G2.NAME as GNAME, " +
            "L.USER_ID as `LIKE` from films f  " +
            "left join FILMS_GENRES fg on f.FILM_ID = fg.FILM_ID " +
            "left join GENRES as G2 on fg.GENRE_ID = G2.GENRE_ID " +
            "left join LIKES L on f.FILM_ID = L.FILM_ID " +
            "left join MPAA M2 on f.MPAA_ID = M2.MPAA_ID " +
            "where f.FILM_ID = ?";

    private static final String SEL_SORT_YEAR_SQL =
            "SELECT df.film_id FROM director_films df " +
                    "JOIN films f ON df.film_id=f.film_id " +
                    "WHERE df.director_id=? ORDER BY f.release_date";
    private static final String SEL_SORT_LIKE_SQL =
            "SELECT df.film_id FROM director_films df " +
                    "LEFT JOIN likes l ON df.film_id=l.film_id " +
                    "WHERE df.director_id=? " +
                    "GROUP BY df.film_id, l.user_id ORDER BY COUNT(L.user_id) DESC";
    private static final String SEARCH_FILMS_SQL =
            "SELECT f.film_id FROM films f " +
                    "WHERE UPPER(f.name) LIKE UPPER('%'||?||'%')";
    private static final String SEARCH_DIRECTOR_SQL =
            "SELECT df.film_id FROM director_films df " +
                    "JOIN directors d ON df.director_id=d.director_id " +
                    "WHERE UPPER(d.name) LIKE UPPER('%'||?||'%')";
    private static final String SEL_COMMON_FILMS_SQL =
            "SELECT f.film_id FROM likes l1 " +
                    "LEFT JOIN likes l2 ON l1.film_id = l2.film_id " +
                    "LEFT JOIN films f ON l1.film_id = f.film_id " +
                    "WHERE l1.user_id=? AND l2.user_id=? AND l1.film_id=l2.film_id";

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, FilmGenreDao filmGenreDao, DirectorFilmsDao directorFilmsDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreDao = filmGenreDao;
        this.directorFilmsDao = directorFilmsDao;
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
            film.setDirectors(directorFilmsDao.getByFilm(film.getId()));
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
            film.setDirectors(directorFilmsDao.getByFilm(film.getId()));

            do {
                if (rs.getString("GNAME") != null) {
                    genres.add(new Genre(rs.getInt("GID"), rs.getString("GNAME")));
                }
                Long l;
                if ((l = rs.getLong("LIKE")) != 0L) {
                    likes.add(l);
                }
            } while (rs.next());
            film.setGenres(genres.stream().collect(Collectors.toList()));
            film.getLikes().addAll(likes);
            getLikesByFilm(film);
            if (film.getGenres().isEmpty()) {
                film.setGenres(null);
            }
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> create(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        film.setId(simpleJdbcInsert.executeAndReturnKey(this.filmToMap(film)).longValue());

        filmGenreDao.updateAllGenreByFilm(film);
        directorFilmsDao.refresh(film);

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

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Long like : film.getLikes()) {
                ps.setLong(1, like);
                ps.setLong(2, film.getId());
                ps.addBatch();
            }
                ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteLikes(Film film) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());

    }

    @Override
    public Film updateFilm(Film film) {
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
            directorFilmsDao.refresh(film);
            updateLikes(film);
        }
        return film;
    }

    @Override
    public List<Film> getMostPopular(Integer count, Integer genreId, Integer date) {
        String sql = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPAA_ID, F.NAME as MPAA_NAME " +
                "from FILMS  F LEFT JOIN  LIKES L on F.FILM_ID  = L.FILM_ID " +
                "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";

        if (genreId == null && date != null) {
            sql = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPAA_ID, F.NAME as MPAA_NAME " +
                    "from FILMS  F LEFT JOIN  LIKES L on F.FILM_ID  = L.FILM_ID " +
                    "WHERE EXTRACT(YEAR FROM RELEASE_DATE) = :date " +
                    "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";
        }

        if (genreId != null && date == null) {
            sql = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPAA_ID, F.NAME as MPAA_NAME" +
                    ", FG.GENRE_ID " +
                    "from FILMS  F " +
                    "LEFT JOIN  LIKES L on F.FILM_ID  = L.FILM_ID " +
                    "LEFT JOIN FILMS_GENRES FG on F.FILM_ID = FG.FILM_ID " +
                    "WHERE FG.GENRE_ID = :genreId " +
                    "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";
        }

        if (genreId != null && date != null) {
            sql = "select F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE,  F.DURATION, F.MPAA_ID, F.NAME as MPAA_NAME " +
                    "from FILMS  F " +
                    "LEFT JOIN  LIKES L on F.FILM_ID  = L.FILM_ID " +
                    "LEFT JOIN FILMS_GENRES FG on F.FILM_ID = FG.FILM_ID " +
                    "WHERE FG.GENRE_ID = :genreId AND EXTRACT(YEAR FROM RELEASE_DATE) = :date " +
                    "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT :count";
        }

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("count", count)
                .addValue("genreId", genreId)
                .addValue("date", date);

        NamedParameterJdbcTemplate nPJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        List<Film> films = nPJdbcTemplate.query(sql,
                namedParameters,
                (rs, rowNum) ->
                {
                    Film film = new Film(
                            rs.getLong("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            (rs.getInt("duration")),
                            new Mpa(rs.getInt("MPAA_ID"), rs.getString("MPAA_NAME"))
                    );
                    getLikesByFilm(film);
                    if (filmGenreDao.findAllByFilmId(film.getId()).isEmpty())  {
                        film.setGenres(null);
                    } else {
                        film.setGenres(new ArrayList<>(filmGenreDao.findAllByFilmId(film.getId())));
                    }

                    return film;
                }
        );
        return films;
    }

    @Override
    public List<Film> getByDirector(Long directorId, String sortBy) {
        if (sortBy.equals("year"))
            return jdbcTemplate.query(SEL_SORT_YEAR_SQL, this::mapFilm, directorId);
        else
            return jdbcTemplate.query(SEL_SORT_LIKE_SQL, this::mapFilm, directorId);
    }

    @Override
    public Collection<Film> search(String queryString, String searchBy) {
        final String searchDirectorTitle = SEARCH_FILMS_SQL + " UNION ALL " + SEARCH_DIRECTOR_SQL;
        final String searchTitleDirector = SEARCH_DIRECTOR_SQL + " UNION ALL " + SEARCH_FILMS_SQL;

        switch (searchBy) {
            case "director":
                return jdbcTemplate.query(SEARCH_DIRECTOR_SQL, this::mapFilm, queryString);
            case "title":
                return jdbcTemplate.query(SEARCH_FILMS_SQL, this::mapFilm, queryString);
            case "director,title":
                return jdbcTemplate.query(searchDirectorTitle, this::mapFilm, queryString, queryString);
            case "title,director":
                return jdbcTemplate.query(searchTitleDirector, this::mapFilm, queryString, queryString);
            default:
                return getMostPopular(10, null, null);
        }
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return jdbcTemplate.query(SEL_COMMON_FILMS_SQL, this::mapFilm, userId, friendId);
    }

    //Использовал существующую логику класса
    private Film mapFilm(ResultSet row, int rowNum) throws SQLException {
        return findById(row.getLong("film_id")).get();
    }
}
