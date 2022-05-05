package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAll();

    Film findById(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void addLike(Film film);

    void remoteLike(Film film);
}
