package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.Review;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        final String sqlQuery = "INSERT INTO REVIEWS(CONTENT, IS_POSITIVE, AUTHOR_ID, FILM_ID) VALUES(?,?,?,?)";
        if (review.getUserId() < 0)
            throw new UserNotFoundException("User with id = " + review.getUserId() + " not found");
        else if (review.getFilmId() < 0)
            throw new FilmNotFoundException("Film with id = " + review.getFilmId() + " not  found");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());

            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE REVIEWS " +
                "SET CONTENT = ?, IS_POSITIVE = ?" +
                "WHERE REVIEW_ID = ?";

        int rowsUpdated = jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (rowsUpdated == 1) {
            log.info("Review with review_id={} has been updated", review.getReviewId());
        } else {
            throw new ReviewNotFoundException(String.format("Review with review_id=%s doesn't exist", review.getReviewId()));
        }
        return review;
    }

    @Override
    public List<Review> getReviewByFilmId(long filmId, int count) {
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USEFUL, FILM_ID, AUTHOR_ID FROM REVIEWS WHERE FILM_ID = ? ORDER BY USEFUL DESC, FILM_ID LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> getReviewByFilmId(int count) {
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USEFUL, FILM_ID, AUTHOR_ID FROM REVIEWS ORDER BY USEFUL DESC, FILM_ID LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);

    }

    @Override
    public Review getReviewById(long id) {
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USEFUL, FILM_ID, AUTHOR_ID FROM REVIEWS WHERE REVIEW_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException(String.format("Review with id=%s doesn't exist", id));
        }

    }

    @Override
    public boolean deleteReview(long id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        int result = jdbcTemplate.update(sqlQuery, id);
        if (result == 1) {
            log.info("Review with id = {} was delete", id);

        } else {
            log.info("Can't delete review with id = {}", id);
        }
        return result == 1;
    }

    public void incrementUseful(long id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL=REVIEWS.USEFUL+1 WHERE REVIEW_ID = ? ";
        jdbcTemplate.update(sqlQuery, id);
    }

    public void decrementUseful(long id) {
        String sqlQuery = "UPDATE REVIEWS SET USEFUL=REVIEWS.USEFUL-1 WHERE REVIEW_ID = ? ";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .withReviewId(resultSet.getLong("review_id"))
                .withContent(resultSet.getString("content"))
                .withIsPositive(resultSet.getBoolean("is_positive"))
                .withUseful(resultSet.getInt("useful"))
                .withUserId(resultSet.getInt("author_id"))
                .withFilmId(resultSet.getInt("film_id"))
                .build();
    }
}
