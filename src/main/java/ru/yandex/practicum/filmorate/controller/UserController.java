package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController("")
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<Friend> getFriends(@PathVariable Long id) {
        return service.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCrossFriend(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCrossFriends(id, otherId);
    }

    @GetMapping("{id}")
    public Optional<User> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Optional<User> createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping
    public Optional<User> updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);

    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("findAll");
        return service.findAll();
    }
}
