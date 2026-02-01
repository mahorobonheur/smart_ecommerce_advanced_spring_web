package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.ReviewDTO;
import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Review;
import com.smart.ecommerce.repository.ReviewRepository;
import com.smart.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Profile("dev")
public class ReviewServiceDevImplementation implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"reviewsPage", "reviewsByProduct"}, allEntries = true)
    public ReviewResponseDTO addReview(ReviewDTO dto) {
        Review review = new Review(dto.getProductId(), dto.getUserId(), dto.getRating(), dto.getComment());
        Review savedReview = reviewRepository.save(review);

        return toResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviewsPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviewsByProduct", key ="#productId")
    public List<Review> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviewsPage", "reviewsByProduct"}, allEntries = true)
    public Review updateReview(String reviewId, int rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviewsPage", "reviewsByProduct"}, allEntries = true)
    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponseDTO toResponse(Review review) {
        return new ReviewResponseDTO(
                review.getReviewId(),
                review.getProductId(),
                review.getUserId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
