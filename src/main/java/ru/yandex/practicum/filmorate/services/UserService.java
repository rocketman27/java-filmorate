package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(long id);

    User addUser(User user);

    User updateUser(User user);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long id);

    List<User> getMutualFriends(long id, long otherId);

    void removeUser(long userId);
}
