package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmService {

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Optional<Film> addFilm(Film film);

    Film updateFilm(Film film);

    void addLike(Long filmId, Long userId);

    void remoteLike(Long filmId, Long userId);

    List<Film> getMostPopular(Integer count);

    void removeFilm(Long filmId);
}
