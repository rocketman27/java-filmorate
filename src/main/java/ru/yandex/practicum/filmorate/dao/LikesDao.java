package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {

    boolean addOrUpdateLike(long userId, long filmId, Integer score);

    boolean deleteLike(long userId, long filmId);
}
