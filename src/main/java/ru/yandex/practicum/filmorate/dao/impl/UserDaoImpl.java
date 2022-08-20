package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    public void removeUser(long userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        int result = jdbcTemplate.update(sqlQuery, userId);
        if (result > 0) {
            log.info("User with userId={} has been deleted", userId);
        } else {
            throw new UserNotFoundException(String.format("Cannot delete user as userId=%s doesn't exist",
                    userId));
        }

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
