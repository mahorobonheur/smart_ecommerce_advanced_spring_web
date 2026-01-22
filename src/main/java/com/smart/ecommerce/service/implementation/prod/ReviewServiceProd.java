package com.smart.ecommerce.service.implementation.prod;

import com.smart.ecommerce.dto.request.ReviewDTO;
import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.model.Review;
import com.smart.ecommerce.repository.ReviewRepository;
import com.smart.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("prod")
public class ReviewServiceProd implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewResponseDTO addReview(ReviewDTO dto) {
        // Create review with safe non-null ID
        Review review = new Review(dto.getProductId(), dto.getUserId(), dto.getRating(), dto.getComment());
        Review savedReview = reviewRepository.save(review);

        return toResponse(savedReview);
    }

    @Override
    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public List<Review> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Review updateReview(String reviewId, int rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    @Override
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
