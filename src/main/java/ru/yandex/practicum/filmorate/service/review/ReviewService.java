package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;


public interface ReviewService {
    void create(Review review) ;
    public Review update(Review review);
    void delete(long id);
    Review findById(long id);
    Collection<Review> findCountByIdFilm(Long filmId, int count);
    void like(long reviewId, long userId);
    void dislike(long reviewId, long userId);
    void deleteLike(long reviewId, long userId);
    void deleteDislike(long reviewId, long userId);

}
