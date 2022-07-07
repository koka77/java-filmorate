package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
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
    private static final String SEL_RECOMMENDATIONS = "SELECT L.FILM_ID FROM " +
            "(SELECT L2.USER_ID, COUNT(L2.FILM_ID) CNT FROM LIKES L1 " +
            "LEFT JOIN LIKES L2 ON L1.FILM_ID = L2.FILM_ID " +
            "WHERE L1.USER_ID = ? AND L1.USER_ID <> L2.USER_ID AND L1.FILM_ID = L2.FILM_ID " +
            "GROUP BY L2.USER_ID ORDER BY CNT DESC LIMIT 1) U " +
            "LEFT JOIN LIKES L ON U.USER_ID = L.USER_ID " +
            "WHERE L.FILM_ID NOT IN (SELECT FILM_ID FROM LIKES WHERE USER_ID = ?) LIMIT ? ";

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
        final String sql = "update USERS set email = ?, login = ?, name = ?, birthday = ?   where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        deleteFriends(user);
        insertFriends(user);
        return user;
    }

    @Override
    public Collection<User> getUserFriends(Long id) {
        final String sqlCnt = "SELECT COUNT(*) From USERS WHERE USER_ID=?";
        if (jdbcTemplate.queryForObject(sqlCnt, Integer.class, id) == 0) {
            throw new ObjectNotFoundException("No data found");
        }

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

    @Override
    public void deleteUser(Long id) {
        final String sql = "DELETE FROM FRIENDS where USER_ID = ? ";
        jdbcTemplate.update(sql, id);
        final String sql2 = "DELETE FROM FRIENDS where FRIEND_ID = ? ";
        jdbcTemplate.update(sql2, id);
        final String sql3 = "DELETE FROM USERS  where USER_ID = ? ";
        jdbcTemplate.update(sql3, id);
    }

    @Override
    public Collection<Long> getRecommendations(Long id, Integer count) {
        return jdbcTemplate.query(SEL_RECOMMENDATIONS,
                (rs, rowNum) -> rs.getLong("FILM_ID"), id, id, count);
    }
}
