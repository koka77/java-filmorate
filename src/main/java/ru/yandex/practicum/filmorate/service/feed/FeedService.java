package ru.yandex.practicum.filmorate.service.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

public interface FeedService {

    Collection<Feed> getAllFeedsByUserId(Long userId, Integer limit);

    void addFeed(String methodName, Object[] parametrs);
}
