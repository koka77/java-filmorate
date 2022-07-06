package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void clear() {
        userService = null;
    }

    @Test
    void shouldReturnTwoUserFriends() throws Exception {
        userController.createUser(TestUtil.validUser1);
        userController.createUser(TestUtil.validUser2);
        userController.createUser(TestUtil.validUser3);
        userController.addFriend(TestUtil.validUser1.getId(), TestUtil.validUser2.getId());
        userController.addFriend(TestUtil.validUser1.getId(), TestUtil.validUser3.getId());
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}/friends", 4l)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":5,\"friends\":[],\"email\":\"validUser2@mail.ru\"," +
                                "\"login\":\"login\",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}," +
                                "{\"id\":6,\"friends\":[],\"email\":\"validUser2@mail.ru\",\"login\":\"login\"," +
                                "\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}]")).andDo(print());
    }

    @Test
    void shouldReturnCrossFriends() throws Exception {
        userController.createUser(TestUtil.validUser1);
        userController.createUser(TestUtil.validUser2);
        userController.createUser(TestUtil.validUser3);
        userController.addFriend(TestUtil.validUser1.getId(), TestUtil.validUser3.getId());
        userController.addFriend(TestUtil.validUser2.getId(), TestUtil.validUser3.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}/friends/common/{otherId}", 8L, 9L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":10,\"friends\":[],\"email\":\"validUser2@mail.ru\"," +
                                "\"login\":\"login\",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}]")).andDo(print());
    }

    @Test
    void shouldReturnUserById() throws Exception {
        userController.createUser(TestUtil.validUser1);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}", 1l)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":1,\"friends\":[],\"email\":\"mail@yandex.ru\"," +
                                "\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\"," +
                                "\"birthday\":\"1976-09-20\"}")).andDo(print());
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        LocalDate tomorrowDate = LocalDate.now().plusDays(1);
        String tomorrowDateStr = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(tomorrowDate);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content("{\"id\":1,\"email\":\"qwe@we.re\",\"login\":\"login\"," +
                                        "\"name\":\"name\",\"birthday\":\"" + tomorrowDateStr + "\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content("{\"id\":1,\"email\":\"\",\"login\":\"login\"," +
                                        "\"name\":\"name\",\"birthday\":\"2021-04-18\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddUserCorrectly() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content("{\"id\":1,\"friends\":[],\"email\":\"mail@yandex.ru\"," +
                                        "\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":7,\"friends\":[],\"email\":\"mail@yandex.ru\"," +
                                "\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}"));
    }

    @Test
    void shouldUpdateUserCorrectly() throws Exception {
        userController.createUser(TestUtil.validUser1);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/users")
                                .content("{\n" +
                                        "  \"login\": \"doloreUpdate\",\n" +
                                        "  \"name\": \"est adipisicing\",\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"email\": \"mail@yandex.ru\",\n" +
                                        "  \"birthday\": \"1976-09-20\"\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\n" +
                                "  \"login\": \"doloreUpdate\",\n" +
                                "  \"name\": \"est adipisicing\",\n" +
                                "  \"id\": 1,\n" +
                                "  \"email\": \"mail@yandex.ru\",\n" +
                                "  \"birthday\": \"1976-09-20\"\n" +
                                "}"));
    }

    @Test
    void shouldReturnAllUsersCorrectly() throws Exception {
        userController.createUser(TestUtil.validUser1);
        userController.createUser(TestUtil.validUser2);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("[{\"id\":1,\"friends\":[],\"email\":\"mail@yandex.ru\"," +
                                "\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}," +
                                "{\"id\":2,\"friends\":[],\"email\":\"validUser1@mail.ru\",\"login\":\"login\"," +
                                "\"name\":\"validUser1\",\"birthday\":\"1981-05-16\"},{\"id\":3,\"friends\":[]," +
                                "\"email\":\"validUser2@mail.ru\",\"login\":\"login\",\"name\":\"validUser2\"," +
                                "\"birthday\":\"1981-05-16\"}]"));
    }

    @Test
    void shouldReturnAllFeedsByUserId() {
//        userService.
    }
}