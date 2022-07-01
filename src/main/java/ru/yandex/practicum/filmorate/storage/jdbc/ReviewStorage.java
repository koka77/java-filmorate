package ru.yandex.practicum.filmorate.storage.jdbc;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    void insertReview(Review review);
    Review update(Review review);
    void delete(long id);
    Optional<Review> findById(long id);
    Collection<Review> findAllByIdFilm(Long filmId, int count);
    void insertLikeOrDislike(long reviewId, long userId, boolean isLike);
    void deleteLikeOrDislike(long reviewId, long userId, boolean isLike);

}
