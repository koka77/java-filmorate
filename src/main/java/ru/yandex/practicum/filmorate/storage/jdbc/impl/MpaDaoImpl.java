package ru.yandex.practicum.filmorate.storage.jdbc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.jdbc.MpaDao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getById(int id) {
        final String sql = "SELECT * FROM MPAA where MPAA_ID = ?";
        return jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                new Mpa(rs.getInt(1), rs.getString(2)), id).findFirst();
    }

    @Override
    public List<Mpa> getAll() {
        final String sql = "SELECT * FROM MPAA ORDER BY 1 asc ";

        return jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                        new Mpa(rs.getInt(1), rs.getString(2)))
                .sorted((o1, o2) -> o1.getId() < o2.getId() ? -1 : 1)
                .collect(Collectors.toList());
    }
}
