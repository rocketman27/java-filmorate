package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {

    boolean addLike(long userId, long filmId);

    boolean deleteLike(long userId, long filmId);
}
