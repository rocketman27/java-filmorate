package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(long id);

    Review getReviewById(long id);

    List<Review> getReviews(int count);

    List<Review> getReviewsByFilmId(long filmId, int count);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    void addDislike(long id, long userId);

    void deleteDislike(long id, long userId);
}
