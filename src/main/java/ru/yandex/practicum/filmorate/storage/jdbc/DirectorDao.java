package ru.yandex.practicum.filmorate.storage.jdbc;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorDao {

    Optional<Director> findById(Long directorId);

    Collection<Director> findAll();

    Long insert(Director director);

    boolean update(Director director);

    boolean delete(Long directorId);
}
