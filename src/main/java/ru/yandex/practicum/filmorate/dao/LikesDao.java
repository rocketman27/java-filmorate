package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;
import java.util.Map;

public interface LikesDao {

    boolean addLike(long userId, long filmId, Integer score);

    boolean deleteLike(long userId, long filmId);

    Map<Long, Map<Long, Integer>> getUsersScores(Collection<Long> userIds);
}
