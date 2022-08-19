package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.LikeForReview;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;

public interface LikesReviewDao {
    void addUserLikesForReview(Review review);

    void addUserLikesForReview(long reviewId, long userId, boolean type);

    void deleteUserLikesByReviewId(long reviewId, long userId, boolean type);

    void deleteUserLikesByReviewId(long reviewId);

    List<LikeForReview> getUserLikesByReviewId(long reviewId);
}
