package ru.yandex.practicum.filmorate.storage.jdbc;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreDao {
    Collection<Genre> findAll();

    Optional<Genre> findById(Integer id);

    void deleteAllByFilmId(Long filmId);

    Optional<Genre> create(Genre genre);

    Optional<Genre> update(Genre genre);

}
