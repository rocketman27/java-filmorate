package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final LikesReviewDao likesReviewDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final FilmDao filmDao;

    @Autowired
    public ReviewServiceImpl(LikesReviewDao likesReviewDao, ReviewDao reviewDao, UserDao userDao, FilmDao filmDao) {
        this.likesReviewDao = likesReviewDao;
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.filmDao = filmDao;
    }

    @Override
    public Review addReview(Review review) {
        if (review == null) {
            log.warn("Received request to update the review=null");
            return null;
        }
        log.info("Received request to add review with id={}", review.getReviewId());
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());
        return reviewDao.addReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        if (review == null) {
            log.warn("Received request to update the review=null");
            return null;
        }
        log.info("Received request to update the review with id={}", review.getReviewId());
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());
        reviewDao.updateReview(review);
        return review;
    }

    @Override
    public boolean deleteReview(long id) {
        log.info("Received request to delete review by id = {}", id);
        return reviewDao.deleteReview(id);

    }

    @Override
    public Review getReviewById(long id) {
        return reviewDao.getReviewById(id);
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        List<Review> reviews;
        if (filmId == null)
            reviews = reviewDao.getReviewsByFilmId(count);
        else
            reviews = reviewDao.getReviewsByFilmId(filmId, count);
        return reviews;
    }

    @Override
    public void addLike(long id, long userId) {
        likesReviewDao.addLikeForReview(id, userId, true);
    }

    @Override
    public void deleteLike(long id, long userId) {
        likesReviewDao.deleteLikeForReview(id, userId, true);

    }

    @Override
    public void addDislike(long id, long userId) {
        likesReviewDao.addLikeForReview(id, userId, false);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        likesReviewDao.deleteLikeForReview(id, userId, false);

    }
}
