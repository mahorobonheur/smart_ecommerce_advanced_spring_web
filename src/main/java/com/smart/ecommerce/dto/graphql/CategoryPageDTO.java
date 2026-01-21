package com.smart.ecommerce.dto.graphql;

import com.smart.ecommerce.dto.response.CategoryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryPageDTO {
    private List<CategoryResponseDTO> categories;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
