package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
    Director getObjectById(Long directorId);

    Collection<Director> getAll();

    Director postDirector(Director director);

    Director putDirector(Director director);

    boolean delDirector(Long directorId);
}
