package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@SpringBootTest
@AutoConfigureMockMvc
class FilmGenreDaoImplTest {

    @Autowired
    FilmGenreDao filmGenreDao;

    @Autowired
    FilmDaoImpl filmDao;

    @Test
    void findById() {
        filmGenreDao.findAllByFilmId(1L).forEach(System.out::println);
        Genre genre = filmGenreDao.findAllByFilmId(1L).stream().findFirst().get();
        assertEquals(new Genre(1, ""),genre);
    }

    @Test
    void addNewGenreToFilm() {
        filmGenreDao.addNewGenreToFilm(1l, new Genre(1, ""));

        filmGenreDao.findAllByFilmId(1L).forEach(System.out::println);
    }


    @Test
    void updateAllGenreByFilm() {
        Film film = filmDao.findById(1l).get();
        film.setGenres(Stream.of(new Genre(1, "")).collect(Collectors.toSet()).stream().collect(Collectors.toList()));
        filmGenreDao.updateAllGenreByFilm(film);
        filmGenreDao.findAllByFilmId(1L).forEach(System.out::println);
    }
}