package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesDao;

@Repository
public class LikesDaoImpl implements LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addLike(long userId, long filmId) throws RuntimeException {
        String sqlQuery = "INSERT INTO LIKES(user_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteLike(long userId, long filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, filmId) > 0;
    }
}
