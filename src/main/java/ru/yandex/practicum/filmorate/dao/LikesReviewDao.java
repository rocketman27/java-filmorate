package ru.yandex.practicum.filmorate.dao;

public interface LikesReviewDao {

    boolean addLikeForReview(long reviewId, long userId, boolean type);

    boolean deleteLikeForReview(long reviewId, long userId, boolean type);

}
