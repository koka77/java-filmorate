package ru.yandex.practicum.filmorate.service.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
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
        Feed result;
        if (parametrs[0].getClass() == Review.class) {
            Review review = (Review) parametrs[0];
            result = createFeed(methodName, review.getUserId(), review.getReviewId());
        } else if (methodName.contains("Like")) {
            result = createFeed(methodName, (Long) parametrs[1], (Long) parametrs[0]);
        } else {
            result = createFeed(methodName, (Long) parametrs[0], (Long) parametrs[1]);
        }

        feedDao.addFeed(result);
    }

    @Override
    public void updateFeedByEventId(Object o) {
        if (o instanceof Review) {
            feedDao.updateFeed(o);
        }

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
// работа с методами из аспекта для review
            case "create":
                feed.setEventType("REVIEW");
                feed.setOperation("ADD");
                break;
            case "update":
                feed.setEventType("REVIEW");
                feed.setOperation("UPDATE");
                break;
            case "delete":
                feed.setEventType("REVIEW");
                feed.setOperation("REMOVE");
                break;
            default:
                throw new UnsupportedOperationException();

        }
        return feed;
    }
}
