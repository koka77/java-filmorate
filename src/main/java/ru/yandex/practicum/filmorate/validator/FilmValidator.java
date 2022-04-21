package ru.yandex.practicum.filmorate.validator;

public interface FilmValidator<T> {
    void validate(T t);
}
