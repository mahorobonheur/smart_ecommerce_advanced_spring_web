package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private String reviewId;
    private String productId;
    private String userId;
    private int rating;
    private String comment;
    private Instant createdAt;
}
