package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class FilmDescriptionLengthValidator implements FilmValidator<Film> {
    @Override
    public void validate(Film film) {
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Длинна описания больше  200 символов");
        }
    }
}
