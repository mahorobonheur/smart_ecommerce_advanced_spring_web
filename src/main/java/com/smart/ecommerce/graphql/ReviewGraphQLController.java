package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.request.ReviewDTO;
import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.model.Review;
import com.smart.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReviewGraphQLController {

    @Autowired
    private ReviewService reviewService;

    @MutationMapping
    @Operation(summary = "GraphQL: Add review")
    public ReviewResponseDTO addReview(@Argument ReviewDTO input) {
        com.smart.ecommerce.dto.response.ReviewResponseDTO dto = reviewService.addReview(input);
        return dto;
    }

    @QueryMapping
    @Operation(summary = "GraphQL: Reviews By Product")
    public List<ReviewResponseDTO> reviewsByProduct(@Argument String productId) {
        return reviewService.getReviewsByProductId(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @MutationMapping
    @Operation(summary = "GraphQL: Update Review")
    public ReviewResponseDTO updateReview(@Argument String reviewId,
                                          @Argument int rating,
                                          @Argument String comment) {
        Review review = reviewService.updateReview(reviewId, rating, comment);
        return toResponse(review);
    }

    @MutationMapping
    @Operation(summary = "GraphQL: Delete Review")
    public Boolean deleteReview(@Argument String reviewId) {
        reviewService.deleteReview(reviewId);
        return true;
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
