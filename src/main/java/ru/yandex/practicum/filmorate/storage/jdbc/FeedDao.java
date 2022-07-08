package ru.yandex.practicum.filmorate.storage.jdbc;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface FeedDao {

    Collection<Feed> findAllFeedsByUserId(Long userId, Integer limit);

    void addFeed(Feed feed);


    Feed findByReview(Review review);

}
