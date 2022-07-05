package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@SpringBootTest
@AutoConfigureMockMvc
public class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected FilmService filmService;

    @Autowired
    protected UserService userService;

}
