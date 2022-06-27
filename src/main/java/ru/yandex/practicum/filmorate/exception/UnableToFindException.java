package ru.yandex.practicum.filmorate.exception;

public class UnableToFindException extends RuntimeException {
    public UnableToFindException() {
        super("Невозможно обновить пользователя с указанным ID");
    }
}
