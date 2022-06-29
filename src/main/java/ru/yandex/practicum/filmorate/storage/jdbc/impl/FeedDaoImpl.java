package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.jdbc.FeedDao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class FeedDaoImpl implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Feed> findAllFeedsByUserId(Long userId, Integer limit) {

        final String sql = "SELECT * FROM FEEDS ORDER BY TIMESTAMP LIMIT ?";
        return jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                        Feed.builder()
                                .eventId(rs.getLong("EVENT_ID"))
                                .userId(rs.getLong("USER_ID"))
                                .eventType(rs.getString("EVENT_TYPE"))
                                .entityId(rs.getLong("ENTITY_ID"))
                                .timestamp(rs.getLong("TIMESTAMP"))
                                .build(),
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