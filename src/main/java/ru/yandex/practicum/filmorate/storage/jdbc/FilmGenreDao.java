package ru.yandex.practicum.filmorate.storage.jdbc;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilmGenreDao {

    Collection<Genre> findAll();

    Optional<Genre> findById(Long id);

    Optional<Genre> add(Genre film);

    Optional<Genre> update(Genre film);
}
