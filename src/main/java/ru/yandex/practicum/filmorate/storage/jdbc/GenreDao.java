package ru.yandex.practicum.filmorate.storage.jdbc;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreDao {
    Collection<Genre> findAll();

    Optional<Genre> findById(Long id);

    Optional<Genre> add(Genre genre);

    Optional<Genre> update(Genre genre);
}
