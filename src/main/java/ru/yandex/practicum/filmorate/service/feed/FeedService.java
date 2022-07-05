package ru.yandex.practicum.filmorate.service.feed;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface FeedService {

    Collection<Feed> getAllFeedsByUserId(Long userId, Integer limit);

    void addFeed(String methodName, Object[] parametrs);

    void addFeed(String methodName, Long reviewId);
    void addFeed(String methodName, Review review);
}
