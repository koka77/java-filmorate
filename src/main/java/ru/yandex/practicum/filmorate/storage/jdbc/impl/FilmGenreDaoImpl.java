package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllByFilmId(Long id) {
        List<Genre> genres = new ArrayList<>();
        String sql = "select * from FILMS_GENRES where film_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            genres.add(Genre.values()[rs.getInt(2) - 1]);
        }
        return genres;
    }

    @Override
    public void addNewGenreToFilm(Long filmId, Genre genre) {
        String sql = " insert into FILMS_GENRES(FILM_ID, GENRE_ID) values  (?, ?)";
        jdbcTemplate.update(sql, filmId, genre.ordinal() + 1);
    }

    @Override
    public void updateAllGenreByFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            // перед обновлением  удаляем устаревшие данные
            String sqlDelete = "delete from FILMS_GENRES where film_id = ?";
            jdbcTemplate.update(sqlDelete, film.getId());

            String sqlUpdate = "insert into FILMS_GENRES(GENRE_ID, FILM_ID) values (?, ?)";

            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlUpdate, film.getId(), genre.ordinal() + 1);
            }
        }
    }
}
