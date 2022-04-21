package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmReleaseDateValidator implements FilmValidator<Film> {
    @Override
    public void validate(Film film) {
        if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза фильма раньше 28.12.1895");
        }
    }
}
