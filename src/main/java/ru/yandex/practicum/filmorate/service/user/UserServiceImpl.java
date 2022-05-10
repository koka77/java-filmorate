package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static Long currentMaxId = 0L;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    private UserStorage storage;
    @Override
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @Override
    public User findById(Integer id) {
        return null;
    }

    @Override
    public User createUser(User user) {
        user.setId(currentMaxId++);
        storage.addUser(user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    @Override
    public void addFriend(User user) {

    }

    @Override
    public void remoteFriend(User user) {

    }

    @Override
    public List<User> getMutualFriends(User user) {
        return null;
    }
}
