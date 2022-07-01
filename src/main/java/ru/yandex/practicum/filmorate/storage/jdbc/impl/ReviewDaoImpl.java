package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.jdbc.ReviewStorage;

import java.lang.module.FindException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
public class ReviewDaoImpl implements ReviewStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final static String FIND_BY_ID = "select * from reviews where review_id = ?";
    private final static String FIND_ALL_REVIEWS = "select * from reviews order by useful desc limit ?";
    private final static String FIND_ALL_REVIEWS_BY_FILM = "select * from reviews where film_id = ? "
            + "order by useful desc limit ?";

    private final static String UPDATE_REVIEW = "update reviews set content = ?, is_positive = ? " +
            "where review_id = ?";

    private final static String DELETE_REVIEW = "delete from reviews where review_id = ?";

    private final static String INSERT_LIKE_REVIEW = "insert into reviews_likes (review_id, user_id, is_like) " +
            "values (?, ?, ?)";
    private final static String DELETE_LIKE_REVIEW = "delete from reviews_likes where review_id = ? and user_id = ?";
    private final static String UPDATE_USEFUL_PLUS = "update reviews set useful = useful + 1 where review_id = ?";
    private final static String UPDATE_USEFUL_MINUS = "update reviews set useful = useful - 1 where review_id = ?";

    @Override
    public void insertReview(Review review) {
        // эти методы здесь потому, что тесты завязаны на id_review
        // нам приходиться делать несколько разных запросов в другие таблицы, чтобы не увеличивать auto increment
        // вемсто того, чтобы просто ловить и обрабатывать исключения
        findUser(review.getUserId());
        findFilm(review.getFilmId());
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("review_id");
            review.setId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        } catch (RuntimeException e) {
            throw new FindException("Кажется, вы пытаетесь сослаться на несуществующий объект. " +
                    "Проверьте filmId и userID");
        }
    }

    @Override
    public Review update(Review review) {
        boolean answ = jdbcTemplate.update(UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getId()) > 0;
        if (!answ) {
            throw new ReviewNotFoundException("Не удалось найти отзыв: " + review.getId());
        }
        //нужно вернуть объект имеено из базы, некторые поля во входящем объекте могут быть неконсистенты
        return findById(review.getId()).get();
    }

    @Override
    public void delete(long id) {
        boolean answ = jdbcTemplate.update(DELETE_REVIEW, id) > 0;
        if (!answ) {
            throw new ReviewNotFoundException("Не удалось найти отзыв: " + id);
        }
    }

    @Override
    public Optional<Review> findById(long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(FIND_BY_ID, id);
        if (rs.next()) {
            Review review = new Review(
                    rs.getLong("review_id"),
                    rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    rs.getLong("user_id"),
                    rs.getLong("film_id"),
                    rs.getInt("useful"));
            return Optional.of(review);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> findAllByIdFilm(Long filmId, int count) {
        Collection<Review> reviews;
        if (filmId == null) {
            reviews = jdbcTemplate.query(FIND_ALL_REVIEWS, (rs, rowNum) -> makeReview(rs), count);
        } else {
            reviews = jdbcTemplate.query(FIND_ALL_REVIEWS_BY_FILM, (rs, rowNum) -> makeReview(rs), filmId, count);
        }
        return reviews;
    }

    @Override
    public void insertLikeOrDislike(long reviewId, long userId, boolean isLike) {
        findUser(userId);
        jdbcTemplate.update(INSERT_LIKE_REVIEW, reviewId, userId, isLike);
        if(isLike) {
            jdbcTemplate.update(UPDATE_USEFUL_PLUS, reviewId);
        } else {
            jdbcTemplate.update(UPDATE_USEFUL_MINUS, reviewId);
        }

    }

    @Override
    public void deleteLikeOrDislike(long reviewId, long userId, boolean isLike) {
        boolean answ = jdbcTemplate.update(DELETE_LIKE_REVIEW, reviewId, userId) > 0;
        if (!answ) {
            throw new FindException("Не удалост найти пару: " + reviewId + ", " + userId);
        }
        if (isLike){
            jdbcTemplate.update(UPDATE_USEFUL_MINUS, reviewId);
        } else {
            jdbcTemplate.update(UPDATE_USEFUL_PLUS, reviewId);
        }
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getLong("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("user_id"),
                rs.getLong("film_id"),
                rs.getInt("useful")
        );
    }

    private void findUser(long userId) {
        SqlRowSet rsUser = jdbcTemplate.queryForRowSet("select user_id from users where user_id = ?",
                userId);
        if (!rsUser.next()) {
            throw new UserNotFoundException("Не найден пользователь: " + userId);
        }
    }
    private void findFilm(long filmId) {
        SqlRowSet rsFilm = jdbcTemplate.queryForRowSet("select film_id from films where film_id = ?",
                filmId);
        if(!rsFilm.next()) {
            throw new FilmNotFoundException(filmId);
        }
    }
}
