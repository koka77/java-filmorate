package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.TestUtil;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldRemoveUserByIdCorrectly() {
        userService.createUser(TestUtil.validUser1);
        userService.createUser(TestUtil.validUser2);


        assertEquals(2, userService.findAll().size());
        userController.deleteUser(1l);
        assertEquals(1, userService.findAll().size());
    }

    @Test
    void shouldReturnTwoUserFriends() throws Exception {
        userController.createUser(TestUtil.validUser1);
        userController.createUser(TestUtil.validUser2);
        userController.createUser(TestUtil.validUser3);
        userController.addFriend(TestUtil.validUser1.getId(), TestUtil.validUser2.getId());
        userController.addFriend(TestUtil.validUser1.getId(), TestUtil.validUser3.getId());
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}/friends", TestUtil.validUser1.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":8,\"friends\":[],\"email\":\"validUser2@mail.ru\"" +
                                ",\"login\":\"login\",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}" +
                                ",{\"id\":7,\"friends\":[],\"email\":\"validUser2@mail.ru\",\"login\":\"login\"" +
                                ",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}]")).andDo(print());
    }

    @Test
    void shouldReturnCrossFriends() throws Exception {
        userController.createUser(TestUtil.validUser1);
        userController.createUser(TestUtil.validUser2);
        userController.createUser(TestUtil.validUser3);
        userController.addFriend(TestUtil.validUser1.getId(), TestUtil.validUser3.getId());
        userController.addFriend(TestUtil.validUser2.getId(), TestUtil.validUser3.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}/friends/common/{otherId}", 10L, 11L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"id\":12,\"friends\":[],\"email\":\"validUser2@mail.ru\"" +
                                ",\"login\":\"login\",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}]"))
                .andDo(print());
    }

    @Test
    void shouldReturnUserById() throws Exception {
        userController.createUser(TestUtil.validUser1);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}", 2l)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"id\":2,\"friends\":[],\"email\":\"validUser2@mail.ru\"" +
                                ",\"login\":\"login\",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}"))
                .andDo(print());
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
                                        "\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\"" +
                                        ",\"birthday\":\"1976-09-20\"}")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json("{\"id\":9,\"friends\":[],\"email\":\"mail@yandex.ru\"" +
                                ",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\"" +
                                ",\"birthday\":\"1976-09-20\"}"));
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
                        .json("[{\"id\":2,\"friends\":[],\"email\":\"validUser2@mail.ru\"" +
                                ",\"login\":\"login\",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}" +
                                ",{\"id\":3,\"friends\":[],\"email\":\"validUser1@mail.ru\",\"login\":\"login\"" +
                                ",\"name\":\"validUser1\",\"birthday\":\"1981-05-16\"},{\"id\":4,\"friends\":[]" +
                                ",\"email\":\"validUser1@mail.ru\",\"login\":\"login\",\"name\":\"validUser1\"" +
                                ",\"birthday\":\"1981-05-16\"},{\"id\":5,\"friends\":[]" +
                                ",\"email\":\"validUser2@mail.ru\",\"login\":\"login\"" +
                                ",\"name\":\"validUser2\",\"birthday\":\"1981-05-16\"}]"));
    }

    @Test
    void shouldReturnAllFeedsByUserId() throws Exception {
        userController.findAll();
        userController.addFriend(2l, 3l);
        userController.removeFriend(2l, 3l);
        userController.getAllFeedsByUserId(2l, 10);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{userId}/feed", 2l)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"eventId\":1,\"userId\":2,\"eventType\":\"FRIEND\"" +
                                ",\"operation\":\"ADD\",\"entityId\":3}" +
                                ",{\"eventId\":2,\"userId\":2,\"eventType\":\"FRIEND\",\"operation\":\"REMOVE\"" +
                                ",\"entityId\":3}]"))
                .andDo(print());
    }


    // Этот тест не работает как надо
    @Test
    void getRecommendations() throws Exception {
        //создал и сохранил 2 фильмма
        Film film1 = TestUtil.validFilm1;
        Film film2 = TestUtil.validFilm1;
        filmService.addFilm(TestUtil.validFilm1);
        filmService.addFilm(TestUtil.validFilm2);

        //ставлю лайки двум фильмам от 2 юзера и одному фильму от другого
        filmService.addLike(1l, 2l);
        filmService.addLike(2l, 2l);
        filmService.addLike(1l, 3l);
        //тут получаю ноль
        userController.getRecommendations(3l, 10);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{id}/recommendations", 3l)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"genres\":null,\"directors\":[],\"rate\":null,\"id\":2,\"likes\":[2],\"name\":\"validFilm2\",\"description\":\"validFilm2 description\",\"releaseDate\":\"2021-10-10\",\"duration\":160,\"mpa\":{\"id\":1,\"name\":\"G\"}}]"))
                .andDo(print());
    }
}