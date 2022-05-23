package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friend;
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
    public User findById(Long id) {
        return storage.findById(id);
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
    public void addFriend(Long id, Long friendId) {
        User userFriend = storage.findById(friendId);
        Friend friend = new Friend(friendId);
        if (!userFriend.getFriends().stream().findFirst().isEmpty()) {
            friend.setCross(true);
        } else {
            friend.setCross(false);
        }
        storage.findById(id).addFriend(friend);
    }

    @Override
    public void removeFriend(Long id, Long userId) {
        storage.findById(id).getFriends().remove(userId);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return storage.getUserFriends(id);
    }

    @Override
    public Collection<User> getCrossFriends(Long id, Long userId) {
        return storage.getUserCrossFriends(id, userId);
    }

    @Override
    public void reset() {
        storage.reset();
    }
}
