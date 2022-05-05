package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class InMemoryUserStorage implements UserStorage {
    private static Integer currentMaxId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Integer id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        return users.put(currentMaxId++, user);
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())){
            users.put(user.getId(), user);
            return user;
        }
        return null;
    }
}
