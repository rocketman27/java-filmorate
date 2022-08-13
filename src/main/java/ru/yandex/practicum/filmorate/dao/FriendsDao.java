package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendsDao {
    void addFriend(long userId, long friendId);

    List<Long> getFriends(long userId);

    void deleteFriend(long userId, long friendId);
}
