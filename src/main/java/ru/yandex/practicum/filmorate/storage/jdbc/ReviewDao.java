package ru.yandex.practicum.filmorate.storage.jdbc;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewDao {
    void insertReview(Review review);
    Review update(Review review);
    void delete(long id);
    Optional<Review> findById(long id);
    Collection<Review> findCount(int count);
    Collection<Review> findByIdFilm(Long filmId, int count);
    void insertLike(long reviewId, long userId);
    void insertDislike(long reviewId, long userId);
    void deleteLike(long reviewId, long userId);
    void deleteDislike(long reviewId, long userId);

}
