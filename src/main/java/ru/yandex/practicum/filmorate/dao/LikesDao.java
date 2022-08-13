package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {

    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);
}
