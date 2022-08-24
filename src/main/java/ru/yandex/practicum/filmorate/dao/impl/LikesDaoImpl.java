package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DaoException;

@Slf4j
@Repository
public class LikesDaoImpl implements LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long userId, long filmId) throws RuntimeException {
        String sqlQuery = "INSERT INTO LIKES(user_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
            log.info("Like by userId={}, filmId={} has been inserted", userId, filmId);
        } catch (DataAccessException e) {
            log.warn("Cannot add like by userId={} for filmId={}", userId, filmId);
        }
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

        int rowsDeleted = jdbcTemplate.update(sqlQuery, userId, filmId);

        if (rowsDeleted == 1) {
            log.info("Like by userId={}, filmId={} has been deleted", userId, filmId);
        } else {
            throw new LikeNotFoundException(String.format("Like by userId=%s, filmId=%s is not found", userId, filmId));
        }
    }
}
