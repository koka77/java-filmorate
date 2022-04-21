package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class TestUtil {
    public static Film validFilm1 = new Film(1, "validFilm1", "validFilm1 description",
            LocalDate.of(2020, 10, 10), Duration.ofMinutes(160));
    public static Film validFilm2 = new Film(2, "validFilm2", "validFilm2 description",
            LocalDate.of(2021, 10, 10), Duration.ofMinutes(160));
    public static Film invalidFilm1 = new Film(1, "invalidFilm1", "invalidFilm1 description",
            LocalDate.of(1894, 10, 1), Duration.ofMinutes(120));
    public static User validUser1 = new User(1, "validUser1@mail.ru", "login", "validUser1",
            LocalDate.of(1981, 5, 16));
    public static User validUser2 = new User(2, "validUser2@mail.ru", "login", "validUser2",
            LocalDate.of(1981, 5, 16));
    public static User invalidUser1 = new User(1, "invalidUser1", "invalidUser1", "name",
            LocalDate.of(1981, 5, 16));

    public static String objectToJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        return mapper.writeValueAsString(object);
    }
}
