package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RestController("")
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public User findById(@PathVariable Long id) {
        log.info("findById: ", id);
        return service.findById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);

    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("findAll");
        return service.findAll();
    }
}
