package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {

    boolean addLike(long userId, long filmId, Integer score);

    boolean deleteLike(long userId, long filmId);
}
