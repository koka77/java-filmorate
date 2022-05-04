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


    //поскольку мы храним фильмы прямо в контроллере, учет ID делаем тут же.
    private static Integer currentMaxId = 0;
    @Autowired
    private List<FilmValidator> validators = new ArrayList<>();



    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validators.forEach(it -> it.validate(film));
        film.setId(currentMaxId++);
        films.put(currentMaxId, film);
        log.info("addFilm: {}", film);
        return film;
    }

    @PutMapping
    public void updateFilm(@Valid @RequestBody Film film) {
        validators.stream().forEach(it -> it.validate(film));
        if (films.containsKey(film.getId())){

        log.debug("updateFilm: {}", film);
        films.put(film.getId(), film);
        }
        log.debug("error updateFilm without ID: {}", film);
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("findAll");
        return films.values().stream().collect(Collectors.toList());
    }

}
