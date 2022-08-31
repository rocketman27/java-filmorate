package ru.yandex.practicum.filmorate.dao;

import org.springframework.data.relational.core.sql.In;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;

public interface ReviewDao {
    Review addReview(Review review);

    void updateReview(Long id, String content, boolean isPositive);

    List<Review> getReviewsByFilmId(long filmId, int count);

    Review getReviewById(long id);

    boolean deleteReview(long id);

    List<Review> getReviewsByFilmId(int count);

}
