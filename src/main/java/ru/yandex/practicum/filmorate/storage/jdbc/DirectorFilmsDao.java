package ru.yandex.practicum.filmorate.storage.jdbc;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public interface DirectorFilmsDao {
    public List<Director> getByFilm(Long filmId);

    public void refresh(Film film);

    public boolean delete(Long filmId);
}
