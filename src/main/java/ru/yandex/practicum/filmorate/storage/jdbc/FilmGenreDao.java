package ru.yandex.practicum.filmorate.storage.jdbc;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

@Repository
public interface FilmGenreDao {

    Set<Genre> findAllByFilmId(Long id);

    void addNewGenreToFilm(Long filmId, Genre genre);

    void updateAllGenreByFilm(Film film);
}
