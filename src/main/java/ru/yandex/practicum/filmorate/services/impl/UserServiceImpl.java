package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventsDao;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.OperationType;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.models.EventType.*;
import static ru.yandex.practicum.filmorate.models.OperationType.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final FriendsDao friendsDao;
    private final FilmDao filmDao;
    private final GenresDao genresDao;
    private final DirectorDao directorDao;
    private final EventsDao eventsDao;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           FriendsDao friendsDao,
                           FilmDao filmDao,
                           GenresDao genresDao,
                           DirectorDao directorDao,
                           EventsDao eventsDao) {
        this.userDao = userDao;
        this.friendsDao = friendsDao;
        this.filmDao = filmDao;
        this.genresDao = genresDao;
        this.directorDao = directorDao;
        this.eventsDao = eventsDao;
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
        Event event = createUserEvent(userId, friendId, ADD);
        eventsDao.addEvent(event);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        log.info("Received request to delete friendId={} of userId={}", friendId, userId);
        friendsDao.deleteFriend(userId, friendId);
        Event event = createUserEvent(userId, friendId, REMOVE);
        eventsDao.addEvent(event);
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("Received request to get a list of friends for userId={}", id);
        userDao.getUserById(id);
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

    @Override
    public List<Film> getRecommendation(long userId) {
        log.info("Received request to get a list of recommendation films for userId={}", userId);
        userDao.getUserById(userId);
        List<Film> films = filmDao.getRecommendation(userId);
        films.forEach(f -> {
            f.setGenres(genresDao.getGenresByFilmId(f.getId()));
            f.setDirectors(directorDao.getDirectorsByFilmId(f.getId()));
        });
        return films;
    }

    @Override
    public List<Event> getFeed(long userId) {
        return eventsDao.getEvents(userId);
    }

    @Override
    public void removeUser(long id) {
        log.info("Received request to delete userId={}", id);
        userDao.removeUser(id);
    }

    private Event createUserEvent(long userId, long friendId, OperationType operationType) {
        return Event.builder()
                    .withUserId(userId)
                    .withEntityId(friendId)
                    .withEventType(FRIEND)
                    .withOperation(operationType)
                    .withTimestamp(Instant.now().toEpochMilli())
                    .build();
    }
}
