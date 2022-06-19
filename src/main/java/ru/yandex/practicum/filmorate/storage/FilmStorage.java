package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {


    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Optional<Film> addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    List<Film> getMostPopular(Integer count);
}
