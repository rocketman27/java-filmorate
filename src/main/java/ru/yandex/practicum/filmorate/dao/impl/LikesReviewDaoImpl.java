package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;

@Slf4j
@Repository
public class LikesReviewDaoImpl implements LikesReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeForReview(long reviewId, long userId, boolean type) {
        String sqlQuery = "INSERT INTO REVIEWS_LIKES(REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?,?,? ) ";

        int updatedRows = jdbcTemplate.update(sqlQuery, reviewId, userId, type);
        if (type == true)
            incrementUseful(reviewId);
        else decrementUseful(reviewId);
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
            decrementUseful(reviewId);
        else incrementUseful(reviewId);

        if (updatedRows == 1) {
            log.info("Like user with id {} from review with id {} was delete ", userId, reviewId);

        } else {
            log.info("Cannot delete Like user with id {} from review with id {} ", userId, reviewId);
        }
    }

    private void incrementUseful(long id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL=REVIEWS.USEFUL+1 WHERE REVIEW_ID = ? ";
        jdbcTemplate.update(sqlQuery, id);
    }

    public void decrementUseful(long id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL=REVIEWS.USEFUL-1 WHERE REVIEW_ID = ? ";
        jdbcTemplate.update(sqlQuery, id);
    }
}
