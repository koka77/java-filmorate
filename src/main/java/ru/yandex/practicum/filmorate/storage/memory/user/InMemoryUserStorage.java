package ru.yandex.practicum.filmorate.storage.memory.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoUserException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Component
public class InMemoryUserStorage implements UserStorage {
    private static Long currentMaxId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User>  findById(Long id) throws NoUserException {
        User user = users.get(id);
        if (user != null) {
            return Optional.of(users.get(id));
        } else {
            throw new NoUserException(String.format("Пользователь с id: " + id + " не существует"));
        }

    }

    @Override
    public Optional<User>  addUser(User user) {
        user.setId(currentMaxId++);
        users.put(user.getId(), user);
        log.info("createUser: {}", user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return Optional.of(user);
        } else {
            throw new NoUserException(String.format("Пользователя: {} не существует", user));
        }
    }

    @Override
    public Collection<Friend> getUserFriends(Long id) {
        return users.get(id).getFriends();
        //return friends.stream().map(friend -> findById(friend.getUserId()).get()).collect(Collectors.toList());

        /*return users.get(id).getFriends().stream()
                .map(friend -> users.get(users.get(friend.getUserId()))).collect(Collectors.toList());*/
    }

    @Override
    public Collection<Friend> getUserCrossFriends(Long userId) {
        return findById(userId).get().getFriends().stream()
                .filter(friend -> friend.isCross()).collect(Collectors.toSet());

    }
}