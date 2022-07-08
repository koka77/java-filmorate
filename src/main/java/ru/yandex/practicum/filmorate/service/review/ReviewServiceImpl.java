package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.ReviewDao;


import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDao reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewStorage,
                             @Qualifier("UserDaoImpl") UserStorage userStorage,
                             @Qualifier("FilmDaoImpl") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public void create(Review review) {
        reviewStorage.insertReview(review);
        log.info("Added a review: {}.", review);
    }

    @Override
    public Review update(Review review) {
        Review reviewFromStorage = reviewStorage.update(review);
        log.debug("Updated review information: {}.", reviewFromStorage);
        return reviewFromStorage;
    }

    @Override
    public void delete(long id) {
        reviewStorage.delete(id);

    }

    @Override
    public Review findById(long id) {
        Optional<Review> review = reviewStorage.findById(id);
        if (review.isEmpty()) {
            throw new ReviewNotFoundException("Review not found");
        }
        log.info("Review: {} found.", review.get().getReviewId());
        return review.get();
    }

    @Override
    public Collection<Review> findCountByIdFilm(Long filmId, int count) {
        Collection<Review> reviews;
        if (filmId == null) {
            reviews = reviewStorage.findCount(count);
            log.info("Returned {} reviews.",reviews.size());
        } else {
            reviews = reviewStorage.findByIdFilm(filmId, count);
            log.info("Returned {} review(s) of the film: {}.", reviews.size(),filmId);
        }
        return reviews;

    }

    @Override
    public void like(long reviewId, long userId) {
        reviewStorage.insertLike(reviewId, userId);
        log.info("User: {} liked the review: {}.", userId, reviewId);
    }

    @Override
    public void dislike(long reviewId, long userId) {
        reviewStorage.insertDislike(reviewId, userId);
        log.info("User: {} disliked the review: {}.", userId, reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        reviewStorage.deleteLike(reviewId, userId);

    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }

}
