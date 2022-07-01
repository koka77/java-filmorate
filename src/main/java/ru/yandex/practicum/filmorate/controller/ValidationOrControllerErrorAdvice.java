package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

@Slf4j
@RestControllerAdvice
public class ValidationOrControllerErrorAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public String validationException(ValidationException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public String noFilmAdvice(ObjectNotFoundException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    public final String illegalIdAdvice(InternalServerException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }
}