package com.smart.ecommerce.dto.mapper;

import com.smart.ecommerce.dto.response.ProductResponseDTO;
import com.smart.ecommerce.model.Product;

public class ProductMapper {

    public static ProductResponseDTO toDto(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStock(),
                product.getInventory().getInventoryId(),
                product.getCategory().getCategoryId(),
                product.getCreatedAt()
        );
    }
}

