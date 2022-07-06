package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController("")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FeedService feedService;

    @Autowired
    public UserController(UserService service, FeedService feedService) {
        this.userService = service;
        this.feedService = feedService;
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.removeUser(id);
    }

    @GetMapping("{userId}/feed")
    public Collection<Feed> getAllFeedsByUserId(@PathVariable Long userId,
                                                @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return feedService.getAllFeedsByUserId(userId, limit);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id < 1 || friendId < 1) {
            throw new UnableToFindException();
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCrossFriend(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCrossFriends(id, otherId);
    }

    @GetMapping("{id}")
    public Optional<User> findById(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            throw new UnableToFindException();
        }
        return userService.findById(id);
    }

    @PostMapping
    public Optional<User> createUser(@Valid @RequestBody User user) {
        if (user.getId() != null && user.getId() < 1) {
            throw new InternalServerException();
        }
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() != null && user.getId() < 1) {
            throw new UnableToFindException();
        }
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("findAll");
        return userService.findAll();
    }
}
