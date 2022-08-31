package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    List<User> getUsers();

    User getUserById(long id);

    User updateUser(User user);

    List<Long> getSimilarUsersIds(long userId, int limit);

    boolean removeUser(long id);
}
