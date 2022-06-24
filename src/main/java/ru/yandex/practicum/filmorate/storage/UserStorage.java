package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(Long id);

    Optional<User>  addUser(User user);

    Optional<User>  updateUser(User user);

    Collection<User> getUserFriends(Long id);

    Collection<User> getUserCrossFriends(Long id, Long otherId);
}
