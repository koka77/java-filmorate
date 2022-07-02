package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
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

    @DeleteMapping("{id}")
    public void removeUser(@PathVariable Long id) {
        service.removeUser(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id < 1 || friendId < 1) {
            throw new ObjectNotFoundException(String.format("Friend not found with id: %s", friendId));
        }
        service.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        Collection<User> users = service.getFriends(id);
            return users;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCrossFriend(@PathVariable Long id, @PathVariable Long otherId) {
        return service.getCrossFriends(id, otherId);
    }

    @GetMapping("{id}")
    public Optional<User> findById(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            throw new ObjectNotFoundException(String.format("User not found with id: %s", id));
        }
        return service.findById(id);
    }

    @PostMapping
    public Optional<User> createUser(@Valid @RequestBody User user) {
        if (user.getId() != null && user.getId() < 1) {
            throw new InternalServerException();
        }
        Optional<User> res =  service.createUser(user);
        return res;
    }

    @PutMapping
    public Optional<User> updateUser(@Valid @RequestBody User user) {
        if (user.getId() != null && user.getId() < 1) {
            throw new ObjectNotFoundException(String.format("User not found with id: %s", user.getId()));
        }
        return service.updateUser(user);

    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("findAll");
        return service.findAll();
    }
}
