package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Optional;

public interface FilmDao extends FilmStorage {

    Optional<Film> findById(Long id);
}
