package ru.yandex.practicum.filmorate.dao;

public interface LikesReviewDao {

    void addLikeForReview(long reviewId, long userId, boolean type);

    void deleteLikeForReview(long reviewId, long userId, boolean type);

}
