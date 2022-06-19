package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.util.Collection;
import java.util.Optional;

public class FilmGenreDaoImpl implements FilmGenreDao {
    @Override
    public Collection<Genre> findAll() {
        return null;
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Genre> add(Genre film) {
        return Optional.empty();
    }

    @Override
    public Optional<Genre> update(Genre film) {
        return Optional.empty();
    }
}
