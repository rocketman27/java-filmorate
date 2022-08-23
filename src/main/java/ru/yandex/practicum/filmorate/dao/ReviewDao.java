package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;

public interface ReviewDao {
    Review addReview(Review review);
    Review updateReview(Review review);
    List<Review> getReviewByFilmId(long filmId, int count);
    Review getReviewById(long id);
    boolean deleteReview(long id);
    List<Review> getReviewByFilmId(int count);
     void incrementUseful(long id);
     void decrementUseful(long id);

}
