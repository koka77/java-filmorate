package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmGenreDao;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.TestUtil.*;

class FilmControllerTest extends AbstractControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private FilmGenreDao filmGenreDao;


    @BeforeEach
    void setUp() {
        filmController.addFilm(validFilm1);
        filmController.addFilm(validFilm2);
        filmService.findAll().forEach(film -> film.getLikes().clear());
    }

    @AfterEach
    void cleanData() {
        filmService.findAll().forEach(film -> film.getLikes().clear());
        filmService.findAll().clear();
        userService = null;
    }

    @Test
    void shouldAddLikeCorrectly() throws Exception {
        userService.createUser(validUser1);
        userService.createUser(validUser2);

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/2"))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1, filmService.findById(1l).get().getLikes().size());
        assertTrue(filmService.findById(1l).get().getLikes().contains(2l));
    }

    @Test
    void shouldReturnCorrectlyNumberOfLikes() throws Exception {

        filmController.addFilm(validFilm3);
        filmController.addFilm(validFilm4);
        filmController.addFilm(validFilm5);
        filmController.addFilm(validFilm6);
        filmController.addFilm(validFilm7);
        filmController.addFilm(validFilm8);
        filmController.addFilm(validFilm9);
        filmController.addFilm(validFilm10);

        validFilm1.getLikes().add(1l);
        validFilm1.getLikes().add(2l);

        validFilm2.getLikes().add(1l);
        validFilm2.getLikes().add(2l);

        validFilm3.getLikes().add(1l);
        validFilm4.getLikes().add(1l);
        validFilm5.getLikes().add(1l);
        validFilm6.getLikes().add(1l);
        validFilm7.getLikes().add(1l);
        validFilm8.getLikes().add(1l);
        validFilm9.getLikes().add(1l);
        validFilm10.getLikes().add(1l);
        validFilm11.getLikes().add(1l);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular?count={count}", 1l))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"genres\":null,\"rate\":null,\"id\":1,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm1\"}}]"));


        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"genres\":null,\"rate\":null,\"id\":1,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm1\"}},{\"genres\":null,\"rate\":null,\"id\":2,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm2\"}},{\"genres\":null,\"rate\":null,\"id\":3,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":4,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":5,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":6,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":7,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":8,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":9,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}},{\"genres\":null,\"rate\":null,\"id\":10,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}}]"));

    }

    @Test
    void shouldReturnFilmById() throws Exception {
        Optional<Film> film = filmController.getFilm(1l);
        System.out.println(film);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/{id}", 1l))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"directors\":[],\"rate\":null,\"id\":1,\"likes\":[2],\"name\":\"New Name\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}}"));
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
/*        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("[{\"genres\":null,\"rate\":null,\"id\":1,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm1\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("{\"id\":1,\"name\":\" \",\"description\":\"description\",\"releaseDate\":\"1895-12-29\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());*/

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("[{\"genres\":null,\"rate\":null,\"id\":1,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":-160,\"mpa\":{\"id\":1,\"name\":\"validFilm1\"}}")
//                                .content("{\"id\":1,\"name\":\"New film\",\"description\":\"description\",\"releaseDate\":\"1895-12-27\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("{\"id\":1,\"name\":\"New film\",\"description\":\"012345678901234567890" +
                                        "123456789012345678901234567890123456789012345678901234567890123456789012" +
                                        "345678901234567890123456789012345678901234567890123456789012345678901234" +
                                        "567890123456789012345678901234567891\"," +
                                        "\"releaseDate\":\"1895-12-29\"," +
                                        "\"duration\":\"123\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddFilmCorrectly() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .content(String.valueOf(objectToJson(validFilm3)))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"genres\":null,\"rate\":null,\"id\":26,\"likes\":[1],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"\"}}"));


    }

    @Test
    void shouldUpdateFilmCorrectly() throws Exception {
        Optional<Film> oldFilm = filmController.getFilm(1L);
        Film newFilm = Film.builder().duration(oldFilm.get().getDuration()).description(oldFilm.get().getDescription())
                .name("New Name").releaseDate(oldFilm.get().getReleaseDate()).mpa(oldFilm.get().getMpa())
                .build();
        newFilm.setId(oldFilm.get().getId());
        String filmAsString = objectToJson(newFilm);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content(filmAsString)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(filmAsString));
    }

    @Test
    void shouldReturnAllFilms() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
//                        .json("[{\"genres\":null,\"rate\":null,\"id\":1,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":2,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}}]"));
                        .json("[{\"genres\":null,\"rate\":null,\"id\":1,\"likes\":[],\"name\":\"New Name\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":2,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":3,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":4,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":5,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":6,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":7,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":8,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":9,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":10,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":11,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":12,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":13,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":14,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":15,\"likes\":[],\"name\":\"validFilm1\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}},{\"genres\":null,\"rate\":null,\"id\":16,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}}]"));
    }

    @Test
    void shouldReturnPopularFilmsWithCorrectGenreAndYear() throws Exception {

        filmController.addFilm(validFilm3);
        filmGenreDao.addNewGenreToFilm(1l, new Genre(1, "Комедия"));
        filmGenreDao.addNewGenreToFilm(1l, new Genre(3, "Мультфильм"));
        filmGenreDao.addNewGenreToFilm(2l, new Genre(2, "Драма"));
        filmGenreDao.addNewGenreToFilm(3l, new Genre(3, "Мультфильм"));

        validFilm1.getLikes().add(1l);
        validFilm1.getLikes().add(2l);

        validFilm2.getLikes().add(1l);
        validFilm2.getLikes().add(2l);

        validFilm3.getLikes().add(1l);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular")
                                .param("count", "2")
                                .param("genreId", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"directors\":null,\"rate\":null,\"id\":1,\"likes\":[2],\"name\":\"New Name\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"New Name\"}},{\"genres\":[{\"id\":3,\"name\":\"Мультфильм\"}],\"directors\":null,\"rate\":null,\"id\":3,\"likes\":[],\"name\":\"validFilm3\",\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm3\"}}]"));
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular")
                                .param("count", "1")
                                .param("date", "2020"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"directors\":null,\"rate\":null,\"id\":1,\"likes\":[2],\"name\":\"New Name\",\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"New Name\"}}]"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular")
                                .param("count", "1")
                                .param("genreId", "2")
                                .param("date", "2021"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"genres\":[{\"id\":2,\"name\":\"Драма\"}],\"directors\":null,\"rate\":null,\"id\":2,\"likes\":[],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"validFilm2\"}}]"));
    }
}
