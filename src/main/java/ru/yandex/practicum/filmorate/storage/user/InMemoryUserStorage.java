package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoUserException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Component
public class InMemoryUserStorage implements UserStorage {
    private static Long currentMaxId = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        user.setId(currentMaxId++);
        users.put(user.getId(), user);
        log.info("createUser: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new NoUserException(String.format("Пользователя с id {} не существует", user.getId()));
        }
    }
}