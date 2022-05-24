package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoUserException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Component
public class InMemoryUserStorage implements UserStorage {
    private static Long currentMaxId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void reset() {
        users.clear();
        currentMaxId = 0L;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) throws NoUserException {
        User user = users.get(id);
        if (user != null) {
            return users.get(id);
        } else {
            throw new NoUserException(String.format("Пользователь с id: " + id + " не существует"));
        }

    }

    @Override
    public User addUser(User user) {
        user.setId(currentMaxId++);
        users.put(user.getId(), user);
        log.info("createUser: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new NoUserException(String.format("Пользователя: {} не существует", user));
        }
    }

    @Override
    public Collection<User> getUserFriends(Long id) {
        Set<Friend> friends = users.get(id).getFriends();
        return friends.stream().map(friend -> findById(friend.getUserId())).collect(Collectors.toList());

        /*return users.get(id).getFriends().stream()
                .map(friend -> users.get(users.get(friend.getUserId()))).collect(Collectors.toList());*/
    }

    @Override
    public Collection<User> getUserCrossFriends(Long id, Long userId) {
        Set<Friend> friendsId = findById(id).getFriends();
        Set<Friend> friendFriendsId = findById(userId).getFriends();
        friendsId.retainAll(findById(userId).getFriends());

        return friendFriendsId.stream().map(friendId -> users.get(friendId)).collect(Collectors.toList());
    }
}