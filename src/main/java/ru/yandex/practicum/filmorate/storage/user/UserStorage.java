package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    void reset();

    Collection<User> findAll();

    User findById(Long id);

    User addUser(User user);

    User updateUser(User user);

    Collection<User> getUserFriends(Long id);

    Collection<User> getUserCrossFriends(Long id, Long userId);
}
