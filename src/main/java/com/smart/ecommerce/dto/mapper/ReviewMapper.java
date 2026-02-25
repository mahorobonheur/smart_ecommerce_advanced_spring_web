package com.smart.ecommerce.dto.mapper;

import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.model.Review;

public class ReviewMapper {
    public static ReviewResponseDTO toDto(Review review){
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
