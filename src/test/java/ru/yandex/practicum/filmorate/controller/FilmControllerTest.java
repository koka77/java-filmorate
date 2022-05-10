package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static ru.yandex.practicum.filmorate.TestUtil.*;

import org.springframework.web.context.annotation.RequestScope;
import ru.yandex.practicum.filmorate.TestUtil;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerTest extends AbstractControllerTest {

    @Autowired
    protected FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController.addFilm(validFilm1);
        filmController.addFilm(validFilm2);
    }

    @AfterEach
    void cleanData() {
        filmController.getService().reset();
    }

    @Test
    void shouldReturnFilmById() throws Exception {
        Film film = filmController.getFilm(1l);
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
                        .json("{\"id\":2," +
                                "\"likes\":[]," +
                                "\"name\":\"validFilm3\"," +
                                "\"description\":\"validFilm3 description\"," +
                                "\"releaseDate\":\"2021-10-10\"," +
                                "\"duration\":\"PT2H40M\"}"));


    }

    @Test
    void shouldUpdateFilmCorrectly() throws Exception {
        Film oldFilm = filmController.getFilm(1L);
        Film newFilm = Film.builder().duration(oldFilm.getDuration()).description(oldFilm.getDescription())
                        .name("New Name").releaseDate(oldFilm.getReleaseDate())
                .build();
        newFilm.setId(oldFilm.getId());
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
