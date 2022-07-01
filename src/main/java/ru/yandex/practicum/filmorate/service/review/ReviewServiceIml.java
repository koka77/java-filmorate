package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnableToFindException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.ReviewStorage;


import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class ReviewServiceIml implements ReviewService {
    ReviewStorage reviewStorage;
    UserStorage userStorage;
    FilmStorage filmStorage;

    @Autowired
    public ReviewServiceIml(ReviewStorage reviewStorage,
                            @Qualifier("UserDaoImpl") UserStorage userStorage,
                            @Qualifier("FilmDaoImpl") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public void create(Review review) {
        reviewStorage.insertReview(review);
        log.info("Added a review: {}", review);
    }

    @Override
    public Review update(Review review) {
        Review reviewFromStorage = reviewStorage.update(review);
        log.debug("Updated review information: {}", reviewFromStorage);
        return reviewFromStorage;
    }

    @Override
    public void delete(long id) {
        reviewStorage.delete(id);
        log.info("The review " + id + " delete.");
    }

    @Override
    public Review findById(long id) {
        Optional<Review> review = reviewStorage.findById(id);
        if (review.isEmpty()) {
            throw new UnableToFindException();
        }
        log.info("Review found: {}", review.get().getId());
        return review.get();
    }

    @Override
    public Collection<Review> findAllByIdFilm(Long filmId, int count) {
        Collection<Review> reviews = reviewStorage.findAllByIdFilm(filmId, count);
        log.info(reviews.size() + " reviews found");
        return reviews;

    }

    @Override
    public void likeOrDislike(long reviewId, long userId, boolean isLike) {
        reviewStorage.insertLikeOrDislike(reviewId, userId, isLike);
        if(isLike) {
            log.info("User: {} liked the review: {}", userId, reviewId);
        } else {
            log.info("User: {} disliked the review: {}", userId, reviewId);
        }

    }

    @Override
    public void deleteLikeOrDislike(long reviewId, long userId, boolean isLike) {
        reviewStorage.deleteLikeOrDislike(reviewId, userId, isLike);
        if(isLike) {
            log.info("User: {} delete like the review: {}", userId, reviewId);
        } else {
            log.info("User: {} delete disliked the review: {}", userId, reviewId);
        }
    }

}
