package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RestController("")
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping("/user")
    public User createUser(@Valid @RequestBody User user) {

        users.put(user.getId(), user);
        log.info("createUser: {}", user);
        return user;
    }

    @PutMapping("/user")
    public User updateUser(@Valid @RequestBody User user) {

        users.put(user.getId(), user);
        log.info("updateUser: {}", user);
        return user;
    }

    @GetMapping("")
    public List<User> findAll() {
        log.info("findAll");
        return users.values().stream().collect(Collectors.toList());
    }


}
