package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.LikesDao;

@Repository
public class LikesDaoImpl implements LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public boolean addLike(long userId, long filmId, Integer score) throws DuplicateKeyException {
        String sqlQuery = "MERGE INTO LIKES(user_id, film_id, score) VALUES (?, ?, ?)";
        boolean successfullyUpdated = jdbcTemplate.update(sqlQuery, userId, filmId, score) > 0;
        if (successfullyUpdated) {
            recalculateAverageScore(filmId);
        }
        return successfullyUpdated;
    }

    @Override
    @Transactional
    public boolean deleteLike(long userId, long filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        int updatedRows = jdbcTemplate.update(sqlQuery, userId, filmId);
        recalculateAverageScore(filmId);
        return updatedRows > 0;
    }

    private void recalculateAverageScore(long filmId) {
        String sqlQuery = "UPDATE films SET rating = (SELECT ROUND(AVG(score), 1) " +
                "FROM likes WHERE film_id = ?) " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, filmId);
    }
}
