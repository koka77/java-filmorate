package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {
    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film findById(Integer id) {
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void addLike(Film film) {

    }

    @Override
    public void remoteLike(Film film) {

    }
}
