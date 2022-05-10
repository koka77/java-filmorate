package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DublicateFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    private static Long currentMaxId = 0L;

    private final List<FilmValidator> validators;

    @Autowired
    public FilmServiceImpl(FilmStorage storage, UserService userService, List<FilmValidator> validators) {
        this.storage = storage;
        this.userService = userService;
        this.validators = validators;
    }

    @Override
    public void reset() {
        currentMaxId = 0L;
        storage.reset();
    }

    @Override
    public Collection<Film> findAll() {
        return storage.findAll();
    }

    @Override
    public Film findById(Long id) {
        return storage.findById(id);
    }

    @Override
    public Film addFilm(Film film) {
        validators.forEach(it -> it.validate(film));
        if (!storage.containsKey(film.getId())) {
            film.setId(currentMaxId++);
            Film res = storage.addFilm(film);
            log.info("addFilm: {}", film);
            return res;
        } else {
            throw new DublicateFilmException(String.format("Фильм с id {} уже существует", film.getId()));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        validators.forEach(it -> it.validate(film));
        log.info("updateFilm: {}", film);
        return storage.updateFilm(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = storage.findById(filmId);
        User user = userService.findById(userId);

        film.addLike(userId);
        log.info("User: {} was like film: {}",user, film);
    }

    @Override
    public void remoteLike(Long filmId, Long userId) {
        Film film = storage.findById(filmId);
        User user = userService.findById(userId);
        film.remoteLike(userId);
        log.info("User: was like film: {}",user, film);
    }
}