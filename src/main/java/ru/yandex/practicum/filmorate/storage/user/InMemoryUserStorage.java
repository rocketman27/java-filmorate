package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Utils;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(Utils.generateUserId());

        if (user.getName().isBlank()) {
            log.info("User's name is blank, setting name = login = {}", user.getLogin());
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("User with id {} has been successfully added", user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("User with id={} doesn't exist", id);
            throw new UserNotFoundException(String.format("User with id=%s doesn't exist", id));
        }
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("User with id {} has been updated", user.getId());
        } else {
            log.warn("User with id {} doesn't exist", user.getId());
            throw new UserNotFoundException(String.format("User with id %s doesn't exist", user.getId()));
        }
        return user;
    }
}
