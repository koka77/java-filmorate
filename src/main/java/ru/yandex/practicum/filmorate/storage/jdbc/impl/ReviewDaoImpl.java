package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.jdbc.ReviewDao;

import java.lang.module.FindException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final static String FIND_BY_ID = "select * from reviews where review_id = ?";
    private final static String FIND_ALL_REVIEWS = "select * from reviews order by useful desc limit ?";
    private final static String FIND_ALL_REVIEWS_BY_FILM = "select * from reviews where film_id = ? " +
            "order by useful desc limit ?";

    private final static String UPDATE_REVIEW = "update reviews set content = ?, is_positive = ? where review_id = ?";

    private final static String DELETE_REVIEW = "delete from reviews where review_id = ?";

    private final static String INSERT_LIKE_REVIEW = "insert into reviews_likes (review_id, user_id, is_like) " +
            "values (?, ?, ?)";
    private final static String DELETE_LIKE_REVIEW = "delete from reviews_likes where review_id = ? and user_id = ?";
    private final static String UPDATE_USEFUL_PLUS = "update reviews set useful = useful + 1 where review_id = ?";
    private final static String UPDATE_USEFUL_MINUS = "update reviews set useful = useful - 1 where review_id = ?";
    private final static String FIND_USER = "select user_id from users where user_id = ?";
    private final static String FIND_FILM = "select film_id from films where film_id = ?";


    @Override
    public void insertReview(Review review) {
       /* эти методы здесь потому, что тесты завязаны на id_review.
        нам приходиться делать несколько разных запросов в другие таблицы, чтобы не увеличивать auto_increment,
        вемсто того, чтобы просто ловить и обрабатывать исключения*/
        findUser(review.getUserId());
        findFilm(review.getFilmId());
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("review_id");
            review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        } catch (RuntimeException e) {
            throw new FindException("Кажется, вы пытаетесь сослаться на несуществующий объект. " +
                    "Проверьте filmId и userID.");
        }
    }

    @Override
    public Review update(Review review) {
        boolean answ = jdbcTemplate.update(UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()) < 1;
        if (answ) {
            throw new ReviewNotFoundException("Не удалось обновить отзыв: " + review.getReviewId());
        }
        //нужно вернуть объект имеено из базы, некторые поля во входящем объекте могут быть неконсистентны
        return findById(review.getReviewId()).get();
    }

    @Override
    public void delete(long id) {
        if (jdbcTemplate.update(DELETE_REVIEW, id) < 1) {
            log.info("Что-то пошло не так. Не получилось удалить отзыв: {}.", +id);
            throw new NoReviewException();
        } else {
            log.info("Review {} delete.", id);
        }
    }

    @Override
    public Optional<Review> findById(long id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_ID, (rs, rowNum) -> makeReview(rs), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> findCount(int count) {
        return jdbcTemplate.query(FIND_ALL_REVIEWS, (rs, rowNum) -> makeReview(rs), count);
    }

    @Override
    public Collection<Review> findByIdFilm(Long filmId, int count) {
        return jdbcTemplate.query(FIND_ALL_REVIEWS_BY_FILM, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public void insertLike(long reviewId, long userId) {
        findUser(userId);
        jdbcTemplate.update(INSERT_LIKE_REVIEW, reviewId, userId, true);
        jdbcTemplate.update(UPDATE_USEFUL_PLUS, reviewId);

    }

    @Override
    public void insertDislike(long reviewId, long userId) {
        findUser(userId);
        jdbcTemplate.update(INSERT_LIKE_REVIEW, reviewId, userId, false);
        jdbcTemplate.update(UPDATE_USEFUL_MINUS, reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        if (jdbcTemplate.update(DELETE_LIKE_REVIEW, reviewId, userId) < 1) {
            log.info("Не удалост удалить пару: " + reviewId + ", " + userId);
        } else {
            jdbcTemplate.update(UPDATE_USEFUL_MINUS, reviewId);
            log.info("User {} delete like the review {}.", userId, reviewId);
        }

    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        if (jdbcTemplate.update(DELETE_LIKE_REVIEW, reviewId, userId) < 1) {
            log.info("Не удалост удалить пару: " + reviewId + ", " + userId);
        } else {
            jdbcTemplate.update(UPDATE_USEFUL_PLUS, reviewId);
            log.info("User: {} delete disliked the review: {}.", userId, reviewId);
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
        SqlRowSet rsUser = jdbcTemplate.queryForRowSet(FIND_USER,
                userId);
        if (!rsUser.next()) {
            throw new UserNotFoundException("Не найден пользователь: " + userId);
        }
    }

    private void findFilm(long filmId) {
        SqlRowSet rsFilm = jdbcTemplate.queryForRowSet(FIND_FILM,
                filmId);
        if (!rsFilm.next()) {
            throw new FilmNotFoundException(filmId);
        }
    }
}
