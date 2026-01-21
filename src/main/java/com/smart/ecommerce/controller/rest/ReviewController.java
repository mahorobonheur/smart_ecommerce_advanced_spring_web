package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.dto.request.ReviewDTO;
import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.model.Review;
import com.smart.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Add review")
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewDTO dto) {
        Review review = reviewService.addReview(dto);
        return ResponseEntity.ok(toResponse(review));
    }

    @GetMapping
    @Operation(summary = "Get all reviews with pagination")
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(Pageable pageable) {
        Page<ReviewResponseDTO> reviews = reviewService.getAllReviews(pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(reviews);
    }


    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews by product")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByProduct(@PathVariable String productId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByProductId(productId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }


    @PutMapping("/{reviewId}")
    @Operation(summary = "Update review")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable String reviewId,
                                                          @RequestBody ReviewDTO dto) {
        Review updated = reviewService.updateReview(reviewId, dto.getRating(), dto.getComment());
        return ResponseEntity.ok(toResponse(updated));
    }


    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
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
