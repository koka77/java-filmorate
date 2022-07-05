package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.jdbc.FeedDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FeedDaoImpl implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Feed> findAllFeedsByUserId(Long userId, Integer limit) {

        final String sql = "SELECT * FROM FEEDS where USER_ID = ? ORDER BY TIMESTAMP LIMIT ?";
        return jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                        Feed.builder()
                                .eventId(rs.getLong("EVENT_ID"))
                                .userId(rs.getLong("USER_ID"))
                                .eventType(rs.getString("EVENT_TYPE"))
                                .entityId(rs.getLong("ENTITY_ID"))
                                .operation(rs.getString("OPERATION"))
                                .timestamp(rs.getLong("TIMESTAMP"))
                                .build(), userId,
                limit > 0 ? limit : 0
        ).collect(Collectors.toList());
    }

    @Override
    public void addFeed(Feed feed) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FEEDS")
                .usingGeneratedKeyColumns("EVENT_ID");

        simpleJdbcInsert.execute(this.feedToMap(feed));

    }



    @Override
    public Feed findByReview(Review r) {
        final String sql = "SELECT * FROM FEEDS where ENTITY_ID = ? AND USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Feed>() {
            @Override
            public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Feed.builder()
                        .eventId(rs.getLong(1))
                        .userId(rs.getLong(2))
                        .timestamp(rs.getLong(3))
                        .eventType(rs.getString(4))
                        .operation(rs.getString(5))
                        .entityId(rs.getLong(6))
                        .build();
            }
        }, r.getReviewId(), r.getUserId());
    }

    public Map<String, Object> feedToMap(Feed feed) {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_ID", feed.getUserId());
        values.put("EVENT_TYPE", feed.getEventType());
        values.put("OPERATION", feed.getOperation());
        values.put("TIMESTAMP", LocalDateTime.now().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());
        values.put("ENTITY_ID", feed.getEntityId());

        return values;
    }
}