package ru.yandex.practicum.filmorate.storage.memory.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnableToFindException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    public Optional<User> findById(Long id) throws UserNotFoundException {
        User user = users.get(id);
        if (user != null) {
            return Optional.of(users.get(id));
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id: " + id + " не существует"));
        }

    }

    @Override
    public Optional<User> addUser(User user) {
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
            throw new UserNotFoundException(String.format("Пользователя: {} не существует", user));
        }
    }

    @Override
    public Collection<User> getUserFriends(Long id) {
        return users.get(id).getFriends();
    }

    @Override
    public Collection<User> getUserCrossFriends(Long id, Long otherId) {

        Long userId = findById(id).get().getId();
        return findById(id).get().getFriends().stream()
                .filter(friend -> friend.getFriends().stream().map(user -> user.getId()).equals(userId))
                .collect(Collectors.toSet());

    }

    @Override
    public Collection<Long> getRecommendations(Long id, Integer count) {
        throw new UnsupportedOperationException();
    }
}