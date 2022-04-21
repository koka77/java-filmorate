package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.TestUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractControllerTest {

    @Autowired
    UserController userController;

    @Test
    void shouldReturnBadRequest() throws Exception {
        LocalDate tomorrowDate = LocalDate.now().plusDays(1);
        String tomorrowDateStr = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(tomorrowDate);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/user")
                                .content("{\"id\":1,\"email\":\"qwe@we.re\",\"login\":\"login\"," +
                                        "\"name\":\"name\",\"birthday\":\"" + tomorrowDateStr + "\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/user")
                                .content("{\"id\":1,\"email\":\"\",\"login\":\"login\"," +
                                        "\"name\":\"name\",\"birthday\":\"2021-04-18\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddUserCorrectly() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/user")
                                .content("{\"id\":1,\"email\":\"asd@fds.ew\",\"login\":\"login\",\"birthday\":\"1981-05-16\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"email\":\"asd@fds.ew\",\"login\":\"login\",\"birthday\":\"1981-05-16\"}"));
    }

    @Test
    void shouldUpdateUserCorrectly() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/user")
                                .content("{\"id\":1,\"email\":\"asd@fds.ew\",\"login\":\"login\",\"birthday\":\"1981-05-16\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":1,\"email\":\"asd@fds.ew\",\"login\":\"login\",\"birthday\":\"1981-05-16\"}"));
    }

    @Test
    void shouldReturnAllUsersCorrectly() throws Exception {
        userController.getUsers().put(TestUtil.validUser1.getId(), TestUtil.validUser1);
        userController.getUsers().put(TestUtil.validUser2.getId(), TestUtil.validUser2);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":1,\"email\":\"validUser1@mail.ru\",\"login\":\"login\"," +
                                "\"nicName\":\"validUser1\",\"birthday\":\"1981-05-16\"}," +
                                "{\"id\":2,\"email\":\"validUser2@mail.ru\",\"login\":\"login\"," +
                                "\"nicName\":\"validUser2\",\"birthday\":\"1981-05-16\"}]"));
    }
}