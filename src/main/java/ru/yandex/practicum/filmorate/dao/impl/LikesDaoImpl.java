package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.models.Score;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<Long, Map<Long, Integer>> getUsersScores(Collection<Long> userIds) {
        Map<Long, Map<Long, Integer>> scores = new HashMap<>();
        userIds.forEach(userId -> scores.put(userId, new HashMap<>()));

        String values = String.join(",", Collections.nCopies(userIds.size(), "?"));
        String sqlQuery = String.format("SELECT user_id, film_id, score " +
                "FROM likes WHERE user_id IN (%s)", values);
        List<Score> queryScores = jdbcTemplate.query(sqlQuery, this::mapRowToScore, userIds.toArray());

        queryScores.forEach(score -> scores.get(score.getUserId()).put(score.getFilmId(), score.getScore()));
        return scores;
    }

    private Score mapRowToScore(ResultSet rs, int rowNum) throws SQLException {
        return new Score(rs.getLong("user_id"), rs.getLong("film_id"),
                rs.getInt("score"));
    }
}
