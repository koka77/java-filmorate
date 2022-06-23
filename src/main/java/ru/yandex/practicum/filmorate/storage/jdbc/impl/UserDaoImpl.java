package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component("UserDaoImpl")
public class UserDaoImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        final String sql = "Select * from users";

        Collection<User> users = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            users.add(new User(rs.getLong("USER_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate()
            ));
        }
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        final String sql = "SELECT * from USERS where user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (rs.next()) {
            return Optional.of(new User(rs.getLong("USER_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate()));
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> addUser(User user) {
        final String SQL_INSERT_USER = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) values (?, ?, ?, ?)";

        int res = jdbcTemplate.update(SQL_INSERT_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        if (res != 1) {
            return Optional.empty();
        } else {
            insertFriends(user);

            return Optional.of(user);
        }
    }

    private void insertFriends(User user) {
        String sql = "insert into FRIENDS (FRIEND_ID, USER_ID, IS_FRIEND) values (?, ?, ?)";

        try (PreparedStatement ps = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql)) {
            for (Friend friend : user.getFriends()) {
                ps.setLong(1, friend.getUserId());
                ps.setLong(2, user.getId());
                ps.setBoolean(3, friend.isCross());
                ps.addBatch();
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> updateUser(User user) {
        return null;
    }

    @Override
    public Collection<Friend> getUserFriends(Long id) {
        final String sql = "Select FRIEND_ID, USER_ID, IS_FRIEND from FRIENDS where user_id = ?";
        Collection<Friend> friends = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            friends.add(new Friend(rs.getLong("FRIEND_ID"),
                    rs.getLong("USER_ID"),
                    rs.getBoolean("IS_FRIEND")
            ));
        }
        return friends;
    }

    @Override
    public Collection<Friend> getUserCrossFriends(Long userId) {
        final String sql = "select * from FRIENDS where USER_ID = ? and IS_FRIEND = true";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId);

        return null;
    }
}
