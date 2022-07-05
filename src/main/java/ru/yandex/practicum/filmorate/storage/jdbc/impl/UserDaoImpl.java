package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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

        try (PreparedStatement ps = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql)) {
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
    public Optional<User> updateUser(User user) {
        final String sql = "update users set email = ?, login = ?, name = ?, " +
                "birthday = ?   where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName()
                , user.getBirthday()
                , user.getId());

        deleteFriends(user);
        insertFriends(user);
        return Optional.of(user);
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

    @Override
    public Collection<Film> getRecommendations(Long id, Integer count) {
        final String sql = "select f.FILM_ID as FILM_ID, " +
                "f.NAME , f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPAA_ID, " +
                "M2.NAME as MPAA_NAME, fg.GENRE_ID as GID, " +
                "G2.NAME as GNAME, " +
                "L.USER_ID as `LIKE` from films f  " +
                "left join FILMS_GENRES fg on f.FILM_ID = fg.FILM_ID " +
                "left join GENRES as G2 on fg.GENRE_ID = G2.GENRE_ID " +
                "left join LIKES L on f.FILM_ID = L.FILM_ID " +
                "left join MPAA M2 on f.MPAA_ID = M2.MPAA_ID " +
                "where f.FILM_ID IN ("
                + " SELECT FILM_ID FROM SELECT USER_ID, FILM_ID, COUNT(FILM_ID) AS weight FROM"
                + " SELECT FILM_ID AS user_film FROM LIKES WHERE USER_ID = ? AS films LEFT OUTER JOIN"
                + " LIKES ON films.user_film = LIKES.FILM_ID GROUP BY LIKES.USER_ID ORDER BY weight DESC AS users"
                + " LEFT OUTER JOIN LIKES ON users.USER_ID = LIKES.USER_ID WHERE LIKES.FILM_ID NOT IN (users.FILM_ID)"
                + " GROUP BY LIKES.FILM_ID ORDER BY users.weight DESC LIMIT ?)";
        Collection<Film> films = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id, count);
        while (rs.next()) {
            Film film = new Film(
                    rs.getLong("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    (rs.getInt("duration")),
                    new Mpa(rs.getInt("MPAA_ID"), rs.getString("MPAA_NAME"))
            );
            films.add(film);
        }
        return films;

    }
}
