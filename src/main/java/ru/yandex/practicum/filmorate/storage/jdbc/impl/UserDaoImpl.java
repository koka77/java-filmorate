package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            User user = new User(rs.getLong("USER_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate());
            user.setFriends((Set<User>) getUserFriends(user.getId()));
            return Optional.of(user);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> addUser(User user) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        user.setId(simpleJdbcInsert.executeAndReturnKey(this.userToMap(user)).longValue());
        insertFriends(user);
        return Optional.of(user);
    }

    public Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", user.getEmail());
        values.put("LOGIN", user.getLogin());
        values.put("NAME", user.getName());
        values.put("BIRTHDAY", user.getBirthday());

        return values;
    }

    private void deleteFriends(User user) {
        final String sql = "DELETE FROM FRIENDS where USER_ID = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    private void insertFriends(User user) {
        if (user.getFriends().isEmpty()) {
            return;
        }
        String sql = "insert into FRIENDS (FRIEND_ID, USER_ID) values (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (User friend : user.getFriends()) {
                ps.setLong(1, friend.getId());
                ps.setLong(2, user.getId());
                ps.addBatch();
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User updateUser(User user) {
        final String sql = "update users set email = ?, login = ?, name = ?, " +
                "birthday = ?   where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName()
                , user.getBirthday()
                , user.getId());

        deleteFriends(user);
        insertFriends(user);
        return user;
    }

    @Override
    public Collection<User> getUserFriends(Long id) {
        final String sql = "SELECT * From USERS where USER_ID IN (SELECT FRIEND_ID FROM FRIENDS where USER_ID = ?)";
        Collection<User> friends = new HashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            friends.add(new User( // Long id, @Valid String email, @Valid String login, String name, LocalDate birthday
                    rs.getLong("USER_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate()
            ));
        }
        return friends;
    }

    @Override
    public Collection<User> getUserCrossFriends(Long id, Long otherId) {
        final String sql = "SELECT * From USERS where USER_ID IN (SELECT FRIEND_ID " +
                "FROM FRIENDS where USER_ID = " + id + ") " +
                "AND USER_ID IN (SELECT FRIEND_ID FROM FRIENDS where USER_ID = " + otherId + ")";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("NAME"),
                rs.getDate("BIRTHDAY").toLocalDate()));

        return users;
    }
}
