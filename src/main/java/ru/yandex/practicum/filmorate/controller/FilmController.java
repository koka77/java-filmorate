package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;


    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        service.addFilm(film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("findAll");
        return service.findAll();
    }

}
