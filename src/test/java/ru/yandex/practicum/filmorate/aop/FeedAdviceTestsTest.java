package ru.yandex.practicum.filmorate.aop;

import lombok.RequiredArgsConstructor;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import ru.yandex.practicum.filmorate.TestUtil;
import ru.yandex.practicum.filmorate.controller.AbstractControllerTest;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.feed.FeedService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DirtiesContext
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FeedAdviceTestsTest extends AbstractControllerTest {

    private User u1 = TestUtil.validUser1;
    private User u2 = TestUtil.validUser2;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    UserController userController;

    @Autowired
    FeedService feedService;

    @Test
    void shouldAddFeedToCreateFriendCorrectly() throws Exception {

        userController.createUser(u1);
        userController.createUser(u2);

        userController.addFriend(u1.getId(), u2.getId());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{userId}/feed", u1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(" [{\"eventId\":1,\"userId\":1,\"eventType\":\"FRIEND\",\"operation\":\"ADD\",\"entityId\":2}]"));

    }

    @Test
    void shouldFeedToRemoveFriendCorrectly() throws Exception {

        userController.removeFriend(u1.getId(), u2.getId());

        feedService.getAllFeedsByUserId(u1.getId(), 10);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{userId}/feed", u1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[{\"eventId\":1,\"userId\":1,\"eventType\":\"FRIEND\",\"operation\":\"ADD\"" +
                                ",\"entityId\":2},{\"eventId\":2,\"userId\":1" +
                                ",\"eventType\":\"FRIEND\",\"operation\":\"REMOVE\",\"entityId\":2}]"));
    }

}
