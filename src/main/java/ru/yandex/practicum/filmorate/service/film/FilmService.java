package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    void reset();

    Collection<Film> findAll();

    Film findById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void addLike(Long filmId, Long userId);

    void remoteLike(Long filmId, Long userId);
}
