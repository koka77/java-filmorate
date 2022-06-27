package ru.yandex.practicum.filmorate.exception;

public class FindFilmException extends RuntimeException {
    public FindFilmException(Long id) {
        super(String.format("Не найден фильм с id: %s", id));
    }
}
