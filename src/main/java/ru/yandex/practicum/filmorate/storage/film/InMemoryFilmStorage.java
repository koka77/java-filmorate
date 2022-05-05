package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static Integer currentMaxId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        return films.put(currentMaxId++, film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        return null;
    }
}
