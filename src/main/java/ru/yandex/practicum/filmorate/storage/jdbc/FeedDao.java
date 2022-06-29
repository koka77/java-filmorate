package ru.yandex.practicum.filmorate.storage.jdbc;

import ru.yandex.practicum.filmorate.model.Feed;
import java.util.Collection;

public interface FeedDao {

    Collection<Feed> findAllFeedsByUserId(Long userId, Integer limit);

    void addFeed(Feed feed);

}
