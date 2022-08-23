package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;


@Slf4j
@Repository
public class LikesReviewDaoImpl implements LikesReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewDao reviewDao;

    @Autowired
    public LikesReviewDaoImpl(JdbcTemplate jdbcTemplate, ReviewDao reviewDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewDao = reviewDao;
    }

    @Override
    public void addLikeForReview(long reviewId, long userId, boolean type) {
        String sqlQuery = "INSERT INTO REVIEWS_LIKES(REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?,?,? ) ";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);
        if (type == true)
            reviewDao.incrementUseful(reviewId);
        else reviewDao.decrementUseful(reviewId);
        if (updatedRows == 1) {
            log.info("Like for user with id {} from review with id {} was added ", userId, reviewId);

        } else {
            log.info("Cannot add like for user with id {} from review with id {} ", userId, reviewId);
        }
    }

    @Override
    public void deleteLikeForReview(long reviewId, long userId, boolean type) {
        String sqlQuery = "DELETE FROM REVIEWS_LIKES WHERE review_id = ? AND user_id = ? AND IS_POSITIVE=?";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);
        if (type == true)
            reviewDao.decrementUseful(reviewId);
        else reviewDao.incrementUseful(reviewId);

        if (updatedRows == 1) {
            log.info("Like user with id {} from review with id {} was delete ", userId, reviewId);

        } else {
            log.info("Cannot delete Like user with id {} from review with id {} ", userId, reviewId);
        }
    }

    @Override
    public void deleteLikeForReview(long reviewId) {
        String sqlQuery = "DELETE FROM REVIEWS_LIKES WHERE review_id = ?";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId);

        if (updatedRows == 1) {
            log.info("Like for user with id {} from review with id {} was delete ", reviewId);

        } else {
            log.info("Cannot delete like for user with id {} from review with id {} ", reviewId);
        }
    }
}
