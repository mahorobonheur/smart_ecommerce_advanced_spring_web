package com.smart.ecommerce.nosql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    private UUID reviewId;

    private UUID productId;
    private UUID userId;

    private int rating;
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
