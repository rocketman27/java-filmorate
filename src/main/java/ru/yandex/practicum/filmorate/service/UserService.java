package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null) {
            log.warn("User with id={} doesn't exist", userId);
            throw new UserNotFoundException(String.format("User with id=%s doesn't exist", userId));
        } else if (friend == null) {
            log.warn("User with id={} doesn't exist, cannot add him to the friends list", friendId);
            throw new UserNotFoundException(String.format("User with id=%s doesn't exist, cannot add him to the friends list", userId));
        } else {
            user.addFriend(friendId);
            friend.addFriend(userId);
            log.info("User with id={} has added a new friend with id={}", userId, friendId);
        }
    }

    public void deleteFriend(long userId, long friendId) {
        userStorage.getUserById(userId)
                   .deleteFriend(friendId);
    }

    public List<User> getFriends(long id) {
        return userStorage.getUserById(id)
                          .getFriends()
                          .stream()
                          .map(userStorage::getUserById)
                          .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(long id, long otherId) {
        List<User> userFriends = getFriends(id);
        List<User> otherUserFriends = getFriends(otherId);

        userFriends.retainAll(otherUserFriends);

        return userFriends;
    }
}
