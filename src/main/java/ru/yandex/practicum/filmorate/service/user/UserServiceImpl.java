package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    private UserStorage storage;
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findById(Integer id) {
        return null;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
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
