package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static ru.yandex.practicum.filmorate.TestUtil.*;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerTest extends AbstractControllerTest {

    @Autowired
    private FilmController filmController;


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

        mockMvc.perform(MockMvcRequestBuilders.put("/films/0/like/1"))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1, filmService.findById(0l).get().getLikes().size());
        assertTrue(filmService.findById(0l).get().getLikes().contains(1l));
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

        validFilm1.getLikes().add(0l);
        validFilm1.getLikes().add(1l);

        validFilm2.getLikes().add(0l);
        validFilm2.getLikes().add(1l);

        validFilm3.getLikes().add(0l);
        validFilm4.getLikes().add(0l);
        validFilm5.getLikes().add(0l);
        validFilm6.getLikes().add(0l);
        validFilm7.getLikes().add(0l);
        validFilm8.getLikes().add(0l);
        validFilm9.getLikes().add(0l);
        validFilm10.getLikes().add(0l);
        validFilm11.getLikes().add(1l);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular?count=1", 1l))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":0," +
                                "\"likes\":[0,1]," +
                                "\"name\":\"validFilm1\"," +
                                "\"description\":\"validFilm1 description\"," +
                                "\"releaseDate\":\"2020-10-10\"," +
                                "\"duration\":\"PT2H40M\"}]"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular?count=2", 1l))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":0,\"likes\":[0,1],\"name\":\"validFilm1\"," +
                                "\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\"," +
                                "\"duration\":\"PT2H40M\"}," +
                                "{\"id\":1,\"likes\":[0,1],\"name\":\"validFilm2\"," +
                                "\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}]"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/films/popular", 1l))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":0,\"likes\":[0,1],\"name\":\"validFilm1\"," +
                                "\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":1,\"likes\":[0,1],\"name\":\"validFilm2\"," +
                                "\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":2,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":3,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":4,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":5,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":6,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":7,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":8,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":9,\"likes\":[0],\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}]"));
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
                        .json("{\"id\":1," +
                                "\"likes\":[]," +
                                "\"name\":\"validFilm2\"," +
                                "\"description\":\"validFilm2 description\"," +
                                "\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}"));
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("{\"id\":1,\"description\":\"description\",\"releaseDate\":\"1895-12-29\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("{\"id\":1,\"name\":\" \",\"description\":\"description\",\"releaseDate\":\"1895-12-29\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("{\"id\":1,\"name\":\"New film\",\"description\":\"description\",\"releaseDate\":\"1895-12-27\",\"filmDuration\":\"PT2H40M\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mockMvc.perform(
                        MockMvcRequestBuilders.put("/films")
                                .content("{\"id\":1,\"name\":\"New film\",\"description\":\"012345678901234567890" +
                                        "123456789012345678901234567890123456789012345678901234567890123456789012" +
                                        "345678901234567890123456789012345678901234567890123456789012345678901234" +
                                        "567890123456789012345678901234567891\"," +
                                        "\"releaseDate\":\"1895-12-29\"," +
                                        "\"duration\":\"PT2H40M\"}")
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
                        .json("{\"id\":3," +
                                "\"likes\":[0]," +
                                "\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\"," +
                                "\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}"));


    }

    @Test
    void shouldUpdateFilmCorrectly() throws Exception {
        Optional<Film> oldFilm = filmController.getFilm(1L);
        Film newFilm = Film.builder().duration(oldFilm.get().getDuration().toMinutes()).description(oldFilm.get().getDescription())
                .name("New Name").releaseDate(oldFilm.get().getReleaseDate())
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
                        .json("{\"id\":1,\"likes\":[],\"name\":\"New Name\"," +
                                "\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}"));
    }

    @Test
    void shouldReturnAllFilms() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":0,\"likes\":[],\"name\":\"validFilm1\"," +
                                "\"description\":\"validFilm1 description\",\"releaseDate\":\"2020-10-10\"," +
                                "\"duration\":\"PT2H40M\"},{\"id\":1,\"likes\":[],\"name\":\"validFilm2\"," +
                                "\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}]"));
    }
}
