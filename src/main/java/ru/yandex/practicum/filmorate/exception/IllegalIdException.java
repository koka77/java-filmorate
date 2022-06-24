package ru.yandex.practicum.filmorate.exception;

public class IllegalIdException extends RuntimeException{
    public IllegalIdException() {
        super("Illegal Id value");
    }
}
