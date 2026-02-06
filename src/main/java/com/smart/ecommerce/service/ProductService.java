package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.ProductDTO;
import com.smart.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    Product addProduct(ProductDTO dto);
    Product getProductById(UUID productId);
    Page<Product> allProducts(Pageable pageable);
    Product updateProduct(UUID productId, ProductDTO dto);
    void deleteProduct(UUID productId);
    Page<Product> findByCategory(String categoryName, Pageable pageable);
    Page<Product> findByProductsRange(double min, double max, Pageable pageable);
    Page<Product> getLowOnStockProduct(int threshold, Pageable pageable);
}
