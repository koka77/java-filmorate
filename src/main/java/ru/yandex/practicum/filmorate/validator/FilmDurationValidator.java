package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmDurationValidator implements FilmValidator<Film> {
    @Override
    public void validate(Film film) {
        if (film.getDuration().isNegative()) {
            throw new ValidationException("Длительность не может быть отрицательной");
        }
    }
}
