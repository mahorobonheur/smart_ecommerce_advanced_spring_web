package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.request.ReviewDTO;
import com.smart.ecommerce.dto.response.ReviewResponseDTO;
import com.smart.ecommerce.dto.response.ReviewsPageDTO;
import com.smart.ecommerce.model.Review;
import com.smart.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReviewGraphQLController {

    @Autowired
    private ReviewService reviewService;

    @MutationMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponseDTO addReview(@Argument ReviewDTO input) {
        com.smart.ecommerce.dto.response.ReviewResponseDTO dto = reviewService.addReview(input);
        return dto;
    }

    @QueryMapping
    @PreAuthorize("permitAll()")
    public ReviewsPageDTO reviewsByProduct(
            @Argument String productId,
            @Argument int page,
            @Argument int size,
            @Argument String sort
    ) {

        String[] sortParts = sort.split(",");
        org.springframework.data.domain.Sort springSort = org.springframework.data.domain.Sort.by(
                sortParts[0]
        );
        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {
            springSort = springSort.descending();
        } else {
            springSort = springSort.ascending();
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, springSort);

        Page<Review> reviewsPage = (Page<Review>) reviewService.getReviewsByProductId(pageable, productId);

        List<ReviewResponseDTO> dtoList = reviewsPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new ReviewsPageDTO(
                dtoList,
                (int) reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages(),
                page,
                size
        );
    }


    @MutationMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponseDTO updateReview(@Argument String reviewId,
                                          @Argument int rating,
                                          @Argument String comment) {
        Review review = reviewService.updateReview(reviewId, rating, comment);
        return toResponse(review);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
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
