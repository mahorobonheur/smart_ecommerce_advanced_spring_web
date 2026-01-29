package com.smart.ecommerce.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String reviewId;

    @NotNull(message = "Product is required to make review")
    private String productId;
    @NotNull(message = "user is required")
    private String userId;

    @Min(value = 1, message = "Minimum rating is 1")
    @Max(value = 5, message = "Maximum rating is 5")
    private int rating;

    private String comment;

    @CreatedDate
    private Instant createdAt;

    public Review(String productId, String userId, int rating, String comment) {
        this.reviewId = UUID.randomUUID().toString();
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Instant.now();
    }
}
