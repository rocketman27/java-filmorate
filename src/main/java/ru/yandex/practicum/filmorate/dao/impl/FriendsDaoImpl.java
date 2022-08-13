package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import java.util.List;

@Slf4j
@Repository
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
            log.info("A friend with userId={} has been added to the list of friends for userId={}", friendId, userId);
        } catch (Exception e) {
            throw new UserNotFoundException(String.format("Cannot add a friend as userId=%s or friendId=%s doesn't exist",
                    userId, friendId));
        }
    }

    @Override
    public List<Long> getFriends(long userId) {
        String sqlQuery = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        int updatedRowsCount = jdbcTemplate.update(sqlQuery, userId, friendId);

        if (updatedRowsCount == 1) {
            log.info("A friend with userId={} has been deleted from the list of friends for userId={}", friendId, userId);
        } else {
            throw new UserNotFoundException(String.format("Cannot delete a friend as userId=%s or friendId=%s doesn't exist",
                    userId, friendId));
        }
    }
}
