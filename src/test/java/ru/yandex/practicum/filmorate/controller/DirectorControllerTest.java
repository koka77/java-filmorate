package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Director;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DirectorControllerTest extends AbstractControllerTest {

    @Autowired
    private DirectorController directorController;

    @Test
    void testGetObjectById() throws Exception {
        directorController.postDirector(new Director(1L, "Director1"));
        Long id = directorController.getAll().stream().findFirst().get().getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/directors/-1"))
                .andExpect(status().isNotFound())
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get("/directors/"+ id))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals("Director1", directorController.getDirector(id).getName());
        directorController.delDirector(id);
    }

    @Test
    void testPostObject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/directors")
                        .content("{\"id\": 10, \"name\": \"Director2\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Long id = directorController.getAll().stream().findFirst().get().getId();
        assertEquals("Director2", directorController.getDirector(id).getName());
        directorController.delDirector(id);
    }

    @Test
    void testPutObject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/directors")
                        .content("{\"id\": 10, \"name\": \"Director unknown\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        directorController.postDirector(new Director(1L, "Director1"));
        Long id = directorController.getAll().stream().findFirst().get().getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/directors")
                        .content("{\"id\": " + id + ", \"name\": \"Director updated\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals("Director updated", directorController.getDirector(id).getName());
        directorController.delDirector(id);
    }
}