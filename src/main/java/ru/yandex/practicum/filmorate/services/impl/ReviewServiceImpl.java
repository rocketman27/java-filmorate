package ru.yandex.practicum.filmorate.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.models.LikeForReview;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final LikesReviewDao likesReviewDao;
   private final ReviewDao reviewDao;

    @Autowired
    public ReviewServiceImpl(LikesReviewDao likesReviewDao, ReviewDao reviewDao) {
        this.likesReviewDao = likesReviewDao;
        this.reviewDao = reviewDao;
    }
    @Override
    public Review addReview(Review review){
        if (review == null) {
            log.warn("Received request to update the review=null");
            return null;
        }
        log.info("Received request to add review with id={}", review.getReviewId());
       review =  reviewDao.addReview(review);
       return review;
    }
    @Override
    public Review updateReview(Review review){
        if (review == null) {
            log.warn("Received request to update the review=null");
            return null;
        }
        log.info("Received request to update the review with id={}", review.getReviewId());
        reviewDao.updateReview(review);
        likesReviewDao.addUserLikesForReview(review);
        return review;
    }
    @Override
    public boolean deleteReview(long id){
        log.info("Received request to delete review by id = {}", id);
        likesReviewDao.deleteUserLikesByReviewId(id);
       return reviewDao.deleteReview(id);

    }
@Override
    public Review getReviewById(long id) {
       Review review = reviewDao.getReviewById(id);
    List<LikeForReview> likesForReview = likesReviewDao.getUserLikesByReviewId(id);
     if(likesForReview!=null)
      review.setLikesForReview(new HashSet<>(likesForReview));
      return review;
    }
    @Override
    public List<Review> getReviews(int count) {
        List<Review> reviews = reviewDao.getReviews(count);
        reviews.stream().forEach(review->review.setLikesForReview(new HashSet<>(likesReviewDao.getUserLikesByReviewId(review.getReviewId()))));
    return reviews;
    }
    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count){
       List<Review> reviews =  reviewDao.getReviews(filmId, count);
       reviews.stream().forEach(review->review.setLikesForReview(new HashSet<>(likesReviewDao.getUserLikesByReviewId(review.getReviewId()))));
       return reviews;
    }
    @Override
    public void addLike(long id, long userId){
        likesReviewDao.addUserLikesForReview(id,userId,true);
        reviewDao.getReviewById(id).incrementUseful();
        reviewDao.incrementUseful(id);

    }
    @Override
    public void deleteLike(long id, long userId){
        likesReviewDao.deleteUserLikesByReviewId(id,userId,true);
        reviewDao.getReviewById(id).decrementUseful();
        reviewDao.decrementUseful(id);

    }
    @Override
    public void addDislike(long id, long userId) {
        likesReviewDao.addUserLikesForReview(id,userId,false);
        reviewDao.getReviewById(id).incrementUseful();
        reviewDao.decrementUseful(id);

    }
    @Override
    public void deleteDislike(long id, long userId){
        likesReviewDao.deleteUserLikesByReviewId(id,userId,false);
        reviewDao.getReviewById(id).decrementUseful();
        reviewDao.incrementUseful(id);

    }
}
