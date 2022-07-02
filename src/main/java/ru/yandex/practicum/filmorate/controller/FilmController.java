package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @DeleteMapping("{filmId}")
    public void removeFilm(@PathVariable Long filmId) {
        service.removeFilm(filmId);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        service.remoteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return service.getMostPopular(count);
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable Long id) {
        if (id < 1) {
            throw new ObjectNotFoundException(String.format("Film not found with id: %s", id));
        }
        Optional<Film> film = service.findById(id);
        return film.orElseThrow(() -> new UnableToFindException());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.getMpa() == null) {
            throw new InternalServerException();
        }
        service.addFilm(film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() != null && film.getId() < 1) {
            throw new ObjectNotFoundException(String.format("Film not found with id: %s", film.getId()));
        }
        return service.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("findAll");
        return service.findAll();
    }

    @GetMapping("director/{directorId}")
    public Collection<Film> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(required = false, defaultValue = "year") String sortBy) {
        log.info("getFilmsByDirector");
        return service.getFilmsByDirector(directorId, sortBy);
    }
}
