package com.smart.ecommerce.dto.graphql;

import com.smart.ecommerce.dto.response.ProductResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductPageDTO {
    private List<ProductResponseDTO> products;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
