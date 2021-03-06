package ru.yandex.practicum.filmorate.storage.memory.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DublicateFilmException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {


    private static Long currentMaxId = 1L;

    private static Map<Long, Film> films = new HashMap<>();

    public void reset() {
        films.clear();
    }

    public boolean containsKey(Long id) {
        return films.containsKey(id);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(Long id) throws FilmNotFoundException {

        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    @Override
    public Optional<Film> create(Film film) {
        film.setId(currentMaxId++);
        if (!films.containsKey(film.getId())) {
            return Optional.of(films.put(film.getId(), film));
        } else throw new DublicateFilmException(String.format("Фильм с id {} уже существует", film.getId()));
    }

    @Override
    public Film updateFilm(Film film) {

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.debug("error updateFilm with ID: {}", film);
            throw new FilmNotFoundException(film.getId());
        }
    }

    @Override
    public List<Film> getMostPopular(Integer count, Integer genreId, Integer date) {

        if (genreId == null && date != null) {
            return films.values().stream()
                    .filter(f -> f.getReleaseDate().getYear() == date)
                    .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }

        if (genreId != null && date == null) {
            return films.values().stream()
                    .filter(f -> f.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId))
                    .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }

        if (genreId != null && date != null) {
            return films.values().stream()
                    .filter(f -> f.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId))
                    .filter(f -> f.getReleaseDate().getYear() == date)
                    .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }

        return films.values().stream().sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public List<Film> getByDirector(Long directorId, String sortBy) {
        throw new InternalServerException("Method not allowed");
    }

    @Override
    public Collection<Film> search(String queryString, String searchBy) {
        throw new InternalServerException("Method not allowed");
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        throw new InternalServerException("Method not allowed");
    }

    @Override
    public void deleteFilm(Long filmId) {
        throw  new UnsupportedOperationException("Метод не поддерживается в данной реализации хранилища");
    }
}
