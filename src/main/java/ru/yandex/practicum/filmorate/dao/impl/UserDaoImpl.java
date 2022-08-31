package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        long filmId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(filmId);
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT user_id, email, login, name, birthday_date FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(long id) {
        String sqlQuery = "SELECT user_id, email, login, name, birthday_date FROM users WHERE user_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User with user_id=%s doesn't exist", id));
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday_date = ? WHERE user_id = ?";

        int recordsUpdatedCount = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (recordsUpdatedCount == 1) {
            return user;
        } else {
            throw new UserNotFoundException(String.format("User with user_id=%s doesn't exist", user.getId()));
        }
    }

    @Override
    public boolean removeUser(long userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sqlQuery, userId) > 0;
    }

    @Override
    public List<Long> getSimilarUsersIds(long userId, int limit) {
        String sqlQuery = "SELECT USER_ID FROM " +
                "(SELECT USER_ID, SUM(FILMS_COUNT) FROM (SELECT USER_ID, COUNT(USER_ID) AS FILMS_COUNT " +
                "FROM LIKES " +
                "WHERE FILM_ID IN (SELECT FILM_ID  " +
                "                        FROM LIKES " +
                "                        WHERE USER_ID = ? " +
                "                        AND SCORE <= 5) AND USER_ID <> 1 AND SCORE <= 5 " +
                "GROUP BY USER_ID " +
                "UNION ALL " +
                "SELECT USER_ID, COUNT(USER_ID) AS FILMS_COUNT " +
                "FROM LIKES " +
                "WHERE FILM_ID IN (SELECT FILM_ID " +
                "                        FROM LIKES " +
                "                        WHERE USER_ID = ? " +
                "                        AND SCORE >= 6) AND USER_ID <> ? AND SCORE >= 6 " +
                "GROUP BY USER_ID) " +
                "GROUP BY USER_ID " +
                "ORDER BY SUM(FILMS_COUNT) " +
                "LIMIT ?" +
                ")";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"),
                userId, userId, userId, limit);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                   .withId(resultSet.getLong("user_id"))
                   .withEmail(resultSet.getString("email"))
                   .withLogin(resultSet.getString("login"))
                   .withName(resultSet.getString("name"))
                   .withBirthday(resultSet.getDate("birthday_date").toLocalDate())
                   .build();
    }
}
