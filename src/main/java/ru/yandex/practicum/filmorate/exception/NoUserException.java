package ru.yandex.practicum.filmorate.exception;

public class NoUserException extends RuntimeException {
    public NoUserException(String message) {
        super(message);
    }
}
