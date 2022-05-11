package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> findAll();

    User findById(Long id);

    User createUser(User user);

    User updateUser(User user);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long userId);

    Collection<User> getFriends(Long id);

    Collection<User> getCrossFriends(Long id, Long userId);

    void reset();
}
