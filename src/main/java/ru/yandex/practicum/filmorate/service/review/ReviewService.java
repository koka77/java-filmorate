package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;


public interface ReviewService {
    void create(Review review) ;
    public Review update(Review review);
    void delete(long id);
    Review findById(long id);
    Collection<Review> findAllByIdFilm(Long filmId, int count);
    void likeOrDislike(long reviewId, long userId, boolean isLike);
    void deleteLikeOrDislike(long reviewId, long userId, boolean isLike);

}
