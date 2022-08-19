package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;
import ru.yandex.practicum.filmorate.models.LikeForReview;
import ru.yandex.practicum.filmorate.models.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class LikesReviewDaoImpl implements LikesReviewDao{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUserLikesForReview(Review review) {
        if (review.getLikesForReview() != null) {
            String sqlQuery1 = "INSERT INTO REVIEWS_USERS_LIKES(REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?, ?, ?)";

            review.getLikesForReview().forEach(like -> jdbcTemplate.update(sqlQuery1, review.getReviewId(), like.getUserId(), like.getType()));
            log.info("Users like for review with review_id={} have been added", review.getReviewId());
        } else {
            log.info("Cannot set up users like for review_id={}, users like is null", review.getReviewId());
        }
    }

    @Override
    public void addUserLikesForReview(long reviewId, long userId, boolean type) {
        String sqlQuery = "INSERT INTO REVIEWS_USERS_LIKES(REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?,?,? ) ";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);

        if (updatedRows == 1) {
            log.info("Like for user with id {} from review with id {} was added ", userId, reviewId);

        } else {
            log.info("Cannot add like for user with id {} from review with id {} ", userId, reviewId);
        }
    }

    @Override
    public void deleteUserLikesByReviewId(long reviewId, long userId, boolean type) {
        String sqlQuery = "DELETE FROM REVIEWS_USERS_LIKES WHERE review_id = ? AND user_id = ? AND IS_POSITIVE=?";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);

        if (updatedRows == 1) {
            log.info("Like user with id {} from review with id {} was delete ", userId, reviewId);

        } else {
            log.info("Cannot delete Like user with id {} from review with id {} ", userId, reviewId);
        }
    }

    @Override
    public void deleteUserLikesByReviewId(long reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS_USERS_LIKES WHERE review_id = ?";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId);

        if (updatedRows == 1) {
            log.info("Like for user with id {} from review with id {} was delete ", reviewId);

        } else {
            log.info("Cannot delete like for user with id {} from review with id {} ", reviewId);
        }
    }

    @Override
    public List<LikeForReview> getUserLikesByReviewId(long reviewId) {
        String sqlQuery = "SELECT * FROM REVIEWS_USERS_LIKES WHERE REVIEW_ID = ? ORDER BY USER_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToLikeForReview, reviewId);
    }

    private LikeForReview mapRowToLikeForReview(ResultSet resultSet, int rowNum) throws SQLException {
        return new LikeForReview(resultSet.getBoolean("is_positive"), resultSet.getLong("user_id"), resultSet.getLong("review_id"));

    }
}
