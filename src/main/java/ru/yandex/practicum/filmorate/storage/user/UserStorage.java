package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    List<User> getUsers();

    User getUserById(long id);

    User updateUser(User user);
}
