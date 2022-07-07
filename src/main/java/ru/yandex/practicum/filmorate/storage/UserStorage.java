package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> addUser(User user);

    User updateUser(User user);

    Collection<User> getUserFriends(Long id);

    Collection<User> getUserCrossFriends(Long id, Long otherId);

    Collection<Long> getRecommendations(Long id, Integer count);
}
