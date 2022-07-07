package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    private final List<FilmValidator> validators;

    @Autowired
    public FilmServiceImpl(@Qualifier("FilmDaoImpl") FilmStorage storage, UserService userService, List<FilmValidator> validators) {
        this.storage = storage;
        this.userService = userService;
        this.validators = validators;
    }

    @Override
    public Collection<Film> findAll() {
        return storage.findAll();
    }

    @Override
    public Optional<Film> findById(Long id) {
        return storage.findById(id);
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        validators.forEach(it -> it.validate(film));
        Optional<Film> res = storage.create(film);
        log.info("addFilm: {}", film);

        return res;
    }

    @Override
    public Film updateFilm(Film film) {
        validators.forEach(it -> it.validate(film));
        log.info("updateFilm: {}", film);
        return storage.updateFilm(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film film = storage.findById(filmId).get();
        User user = userService.findById(userId).get();

        film.addLike(userId);
        storage.updateFilm(film);
        log.info("User: {} was like film: {}", user, film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        if (filmId < 1 || userId < 1) {
            throw new UnableToFindException();
        }
        Film film = storage.findById(filmId).get();
        User user = userService.findById(userId).get();
        film.removeLike(userId);
        storage.updateFilm(film);
        log.info("User: was like film: {}", user, film);
    }

    @Override
    public List<Film> getMostPopular(Integer count, Integer genreId, Integer date) {
        return storage.getMostPopular(count, genreId, date);
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        List<Film> films = storage.getByDirector(directorId, sortBy);
        if (films.size() == 0) {
            throw new ObjectNotFoundException(String.format("Films not found for director: %s", directorId));
        }
        return storage.getByDirector(directorId, sortBy);
    }

    @Override
    public Collection<Film> searchFilms(String queryString, String searchBy) {
        return storage.search(queryString, searchBy);
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }
}