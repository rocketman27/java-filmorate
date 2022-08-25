package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendsDao;

import java.util.List;

@Repository
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Long> getFriends(long userId) {
        String sqlQuery = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;
    }
}
