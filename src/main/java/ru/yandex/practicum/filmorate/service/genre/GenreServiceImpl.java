package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.GenreDao;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreServiceImpl(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Override
    public Collection<Genre> findAll() {
        return genreDao.findAll();
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return genreDao.findById(id);
    }

    @Override
    public void deleteAllByFilmId(Long filmId) {
        genreDao.deleteAllByFilmId(filmId);
    }

    @Override
    public Optional<Genre> create(Genre genre) {
        return genreDao.create(genre);
    }

    @Override
    public Optional<Genre> update(Genre genre) {
        return genreDao.update(genre);
    }

    @Override
    public void updateAllByFilm(Film film) {
    }
}
