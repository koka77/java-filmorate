package ru.yandex.practicum.filmorate.service.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.jdbc.FeedDao;
import ru.yandex.practicum.filmorate.storage.jdbc.ReviewDao;

import java.util.Collection;
import java.util.Optional;

@Service
public class FeedServiceImpl implements FeedService {

    private final FeedDao feedDao;
    private final ReviewDao reviewDao;

    private Feed feed;

    @Autowired
    public FeedServiceImpl(FeedDao feedDao, ReviewDao reviewDao) {
        this.feedDao = feedDao;
        this.reviewDao = reviewDao;
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
    public void addFeed(String methodName, Long reviewId) {
        Optional<Review> review = reviewDao.findById(reviewId);

        if (review.isPresent()) {
            Feed feed = feedDao.findByReview(review.get());
            feed = createFeed(methodName, feed.getUserId(), feed.getEntityId());
            feedDao.addFeed(feed);

        }
    }

    @Override
    public void addFeed(String methodName, Review r) {
        Feed feed = feedDao.findByReview(r);
//        Feed feed = createFeed(methodName,  r.getReviewId(), r.getUserId() );
        feed.setOperation("UPDATE");
        feed.setEventType("REVIEW");
        feedDao.addFeed(feed);
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
