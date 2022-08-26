package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventsDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.OperationType;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.models.EventType.*;
import static ru.yandex.practicum.filmorate.models.OperationType.*;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final LikesReviewDao likesReviewDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final FilmDao filmDao;
    private final EventsDao eventsDao;

    @Autowired
    public ReviewServiceImpl(LikesReviewDao likesReviewDao, ReviewDao reviewDao,
                             UserDao userDao, FilmDao filmDao, EventsDao eventsDao) {
        this.likesReviewDao = likesReviewDao;
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.filmDao = filmDao;
        this.eventsDao = eventsDao;
    }

    @Override
    public Review addReview(Review review) {
        if (review == null) {
            log.warn("Received request to update the review=null");
            return null;
        }
        log.info("Received request to add review");

        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());

        review = reviewDao.addReview(review);
        Event event = createReviewEvent(review, ADD);
        eventsDao.addEvent(event);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        if (review == null) {
            log.warn("Received request to update the review=null");
            return null;
        }
        log.info("Received request to update the review with id={}", review.getReviewId());

        reviewDao.updateReview(review);
        log.info("Review with review_id={} has been updated", review.getReviewId());
        Review updatedReview = reviewDao.getReviewById(review.getReviewId());
        Event event = createReviewEvent(updatedReview, UPDATE);
        eventsDao.addEvent(event);

        return review;
    }

    @Override
    public boolean deleteReview(long id) {
        log.info("Received request to delete review by id = {}", id);
        Review review = reviewDao.getReviewById(id);
        Event event = createReviewEvent(review, REMOVE);
        eventsDao.addEvent(event);
        boolean successfullyDeleted = reviewDao.deleteReview(id);
        if (successfullyDeleted) {
            log.info("Review with id = {} was deleted", id);
            return true;
        } else {
            log.info("Can't delete review with id = {}", id);
            return false;
        }
    }

    @Override
    public Review getReviewById(long id) {
        return reviewDao.getReviewById(id);
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        List<Review> reviews;
        if (filmId == null) {
            reviews = reviewDao.getReviewsByFilmId(count);
        } else {
            reviews = reviewDao.getReviewsByFilmId(filmId, count);
        }
        return reviews;
    }

    @Override
    public void addLike(long id, long userId) {
        boolean successfullyAdded = likesReviewDao.addLikeForReview(id, userId, true);
        if (successfullyAdded) {
            log.info("Like user with id {} from review with id {} was added", userId, id);
        } else {
            log.info("Cannot add Like user with id {} from review with id {} ", userId, id);
        }
    }

    @Override
    public void deleteLike(long id, long userId) {
        boolean successfullyDeleted = likesReviewDao.deleteLikeForReview(id, userId, true);
        if (successfullyDeleted) {
            log.info("Like for user with id {} from review with id {} was deleted ", userId, id);
        } else {
            log.info("Cannot delete like for user with id {} from review with id {} ", userId, id);
        }
    }

    @Override
    public void addDislike(long id, long userId) {
        boolean successfullyAdded = likesReviewDao.addLikeForReview(id, userId, false);
        if (successfullyAdded) {
            log.info("Dislike for user with id {} from review with id {} was deleted ", userId, id);
        } else {
            log.info("Cannot delete dislike for user with id {} from review with id {} ", userId, id);
        }
    }

    @Override
    public void deleteDislike(long id, long userId) {
        boolean successfullyDeleted = likesReviewDao.deleteLikeForReview(id, userId, false);
        if (successfullyDeleted) {
            log.info("Dislike for user with id {} from review with id {} was deleted ", userId, id);
        } else {
            log.info("Cannot delete dislike for user with id {} from review with id {} ", userId, id);
        }
    }

    private Event createReviewEvent(Review review, OperationType operation) {
        return Event.builder()
                    .withUserId(review.getUserId())
                    .withEntityId(review.getReviewId())
                    .withEventType(REVIEW)
                    .withOperation(operation)
                    .withTimestamp(Instant.now().toEpochMilli())
                    .build();
    }
}
