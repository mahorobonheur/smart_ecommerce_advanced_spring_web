package com.smart.ecommerce.dto.response;

import java.util.List;

public record ReviewsPageDTO(
        List<ReviewResponseDTO> reviews,
        int totalElements,
        int totalPages,
        int page,
        int size
) {}
