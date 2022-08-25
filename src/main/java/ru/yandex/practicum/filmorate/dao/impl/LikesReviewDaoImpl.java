package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;

@Repository
public class LikesReviewDaoImpl implements LikesReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addLikeForReview(long reviewId, long userId, boolean type) {
        String sqlQuery = "INSERT INTO REVIEWS_LIKES(REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?,?,? ) ";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);
        if (updatedRows == 0) {
            return false;
        }
        if (type) {
            incrementUseful(reviewId);
        } else {
            decrementUseful(reviewId);
        }

        return updatedRows > 0;
    }

    @Override
    public boolean deleteLikeForReview(long reviewId, long userId, boolean type) {
        String sqlQuery = "DELETE FROM REVIEWS_LIKES WHERE review_id = ? AND user_id = ? AND IS_POSITIVE=?";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);
        if (updatedRows == 0) {
            return false;
        }
        if (type) {
            decrementUseful(reviewId);
        } else {
            incrementUseful(reviewId);
        }

        return updatedRows > 0;
    }

    private void incrementUseful(long id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL=REVIEWS.USEFUL+1 WHERE REVIEW_ID = ? ";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void decrementUseful(long id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL=REVIEWS.USEFUL-1 WHERE REVIEW_ID = ? ";
        jdbcTemplate.update(sqlQuery, id);
    }
}
