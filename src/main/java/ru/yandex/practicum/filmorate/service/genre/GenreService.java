package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreService {

    Collection<Genre> findAll();

    Optional<Genre> findById(Integer id);

    void deleteAllByFilmId(Long filmId);

    Optional<Genre> create(Genre genre);

    Optional<Genre> update(Genre genre);

    void updateAllByFilm(Film film);


}
