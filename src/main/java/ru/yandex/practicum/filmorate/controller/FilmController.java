package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @Autowired
    List<FilmValidator> validators = new ArrayList<>();



    @PostMapping("/film")
    public Film addFilm(@Valid @RequestBody Film film) {
        validators.stream().forEach(it -> it.validate(film));
        films.put(film.getId(), film);
        log.info("addFilm: {}", film);
        return film;
    }

    @PutMapping("/film")
    public Film updateFilm(@Valid @RequestBody Film film) {
        validators.stream().forEach(it -> it.validate(film));
        log.debug("updateFilm: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("findAll");
        return films.values().stream().collect(Collectors.toList());
    }

}
