package ru.yandex.practicum.filmorate.service.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.jdbc.FeedDao;

import java.util.Collection;

@Service
public class FeedServiceImpl implements FeedService {

    private final FeedDao feedDao;

    private Feed feed;

    @Autowired
    public FeedServiceImpl(FeedDao feedDao) {
        this.feedDao = feedDao;
    }

    @Override
    public Collection<Feed> getAllFeedsByUserId(Long userId, Integer limit) {
        return feedDao.findAllFeedsByUserId(userId, limit);
    }

    @Override
    public void addFeed(String methodName, Object[] parametrs) {

        Feed result = createFeed(methodName, (Long) parametrs[0], (Long) parametrs[1]);
        feedDao.addFeed(result);
    }



    private Feed createFeed(String methodName, Long userId, Long entityId) {

        Feed feed = Feed.builder().userId(userId).entityId(entityId).build();

        switch (methodName) {

            case "addFriend":
                feed.setEventType("FRIEND");
                feed.setOperation("ADD");
                break;
            case "removeFriend":
                feed.setEventType("FRIEND");
                feed.setOperation("REMOVE");
                break;
            case "addLike":
                feed.setEventType("LIKE");
                feed.setOperation("ADD");
                break;
            case "removeLike":
                feed.setEventType("LIKE");
                feed.setOperation("REMOVE");
                break;
            case "addReview":
                feed.setEventType("REVIEW");
                feed.setOperation("ADD");
                break;
            case "updateReview":
                feed.setEventType("REVIEW");
                feed.setOperation("UPDATE");
                break;
            case "removeReview":
                feed.setEventType("REVIEW");
                feed.setOperation("REMOVE");
                break;
            default:
                throw new UnsupportedOperationException();

        }
        return feed;
    }
}
