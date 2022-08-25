package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendsDao {
    boolean addFriend(long userId, long friendId);

    List<Long> getFriends(long userId);

    boolean deleteFriend(long userId, long friendId);
}
