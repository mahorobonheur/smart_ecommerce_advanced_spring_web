package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.ReviewDTO;
import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO addReview(ReviewDTO dto);
    Page<Review> getAllReviews(Pageable pageable);
    List<Review> getReviewsByProductId(String productId);
    Review updateReview(String reviewId, int rating, String comment);
    void deleteReview(String reviewId);
}
