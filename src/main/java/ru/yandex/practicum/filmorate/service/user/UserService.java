package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> findAll();

    User findById(Integer id);

    User createUser(User user);

    User updateUser(User user);

    void addFriend(User user);

    void remoteFriend(User user);

    List<User> getMutualFriends(User user);
}
