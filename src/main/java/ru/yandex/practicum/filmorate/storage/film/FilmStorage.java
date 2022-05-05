package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film findById(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film film);
}
