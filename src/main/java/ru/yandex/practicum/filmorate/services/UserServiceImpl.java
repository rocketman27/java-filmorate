package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final FriendsDao friendsDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, FriendsDao friendsDao) {
        this.userDao = userDao;
        this.friendsDao = friendsDao;
    }

    @Override
    public List<User> getUsers() {
        log.info("Received request to get all users");
        return userDao.getUsers();
    }

    @Override
    public User getUserById(long id) {
        log.info("Received request to get a user by userId={}", id);
        return userDao.getUserById(id);
    }

    @Override
    public User addUser(User user) {
        log.info("Received request to add user");
        if (user.getName().isBlank()) {
            log.info("User's name is blank, setting name = login = {}", user.getLogin());
            user.setName(user.getLogin());
        }

        user = userDao.addUser(user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Received request to update user");
        return userDao.updateUser(user);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        log.info("Received request to add friendId={} for userId={}", friendId, userId);
        friendsDao.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        log.info("Received request to delete friendId={} of userId={}", friendId, userId);
        friendsDao.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("Received request to get a list of friends for userId={}", id);
        List<Long> friendsIds = friendsDao.getFriends(id);
        if (friendsIds.isEmpty()) {
            return new ArrayList<>();
        } else {
            return friendsIds.stream()
                             .map(userDao::getUserById)
                             .collect(Collectors.toList());
        }
    }

    @Override
    public List<User> getMutualFriends(long id, long otherId) {
        log.info("Received request to get a list of  mutual friends for userId={} and other userId={}", id, otherId);
        List<User> userFriends = getFriends(id);
        List<User> otherUserFriends = getFriends(otherId);

        userFriends.retainAll(otherUserFriends);

        return userFriends;
    }
}
