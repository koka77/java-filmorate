package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserServiceImpl(@Qualifier("UserDaoImpl") UserStorage storage,
                           @Qualifier("FilmDaoImpl") FilmStorage filmStorage) {
        this.storage = storage;
        this.filmStorage = filmStorage;
    }

    private UserStorage storage;
    private FilmStorage filmStorage;

    @Override
    public Collection<User> findAll() {
        return storage.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return storage.findById(id);
    }

    @Override
    public Optional<User> createUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        storage.addUser(user);

        return Optional.of(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return storage.updateUser(user);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        User friend = storage.findById(friendId).get();
        User user = storage.findById(id).get();
        user.addFriend(friend);
        storage.updateUser(user);
    }

    @Override
    public void removeFriend(Long id, Long userId) {
        User user = storage.findById(id).get();
        user.setFriends(user.getFriends().stream().filter(user1 -> user1.getId() != userId)
                .collect(Collectors.toSet()));
        storage.updateUser(user);

    }

    @Override
    public Collection<User> getFriends(Long id) {
        return storage.getUserFriends(id);
    }

    @Override
    public Collection<User> getCrossFriends(Long id, Long otherId) {
        return storage.getUserCrossFriends(id, otherId);
    }

    @Override
    public Collection<Film> getRecommendations(Long id, Integer count) {
        Collection<Film> recommendationsFilms = new HashSet<>();
        Collection<Long> filmsId = storage.getRecommendations(id, count);
        recommendationsFilms = filmsId.stream()
                .map(filmId -> filmStorage.findById(filmId))
                .map(Optional :: get)
                .collect(Collectors.toSet());
        return recommendationsFilms;
    }
}
