package com.smart.ecommerce.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String reviewId;
    private String productId;
    private String userId;

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;

    @CreatedDate
    private Instant createdAt = Instant.now();
}
