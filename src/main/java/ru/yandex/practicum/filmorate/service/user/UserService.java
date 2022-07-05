package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Collection<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> createUser(User user);

    Optional<User> updateUser(User user);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long userId);

    Collection<User> getFriends(Long id);

    Collection<User> getCrossFriends(Long id, Long otherId);

    Collection<Film> getRecommendations(Long id, Integer count);
}
