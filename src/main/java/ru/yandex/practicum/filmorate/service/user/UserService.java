package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    void addFriend(User user);

    void remoteFriend(User user);

    List<User> getMutualFriends(User user);
}
