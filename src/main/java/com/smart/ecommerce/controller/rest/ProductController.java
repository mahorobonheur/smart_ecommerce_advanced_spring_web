package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.dto.request.ProductDTO;
import com.smart.ecommerce.dto.response.ProductResponseDTO;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    @Operation(summary = "Adding product api")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductDTO productDTO){
        Product product = productService.addProduct(productDTO);
        return ResponseEntity.ok(toResponse(product));
    }

    @GetMapping("{productId}")
    @Operation(summary = "Get product by id")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(toResponse(product));
    }

    @GetMapping("/by-category/{categoryName}")
    public ResponseEntity<Page<ProductResponseDTO>> getByCategoryName(@PathVariable String categoryName, Pageable pageable){
        Page<ProductResponseDTO> products = productService.findByCategory(categoryName, pageable).map(this::toResponse);
        return ResponseEntity.ok(products);

    }

    @GetMapping("/between")
    public ResponseEntity <Page<ProductResponseDTO>> findByPriceRange(@RequestParam double min, @RequestParam double max, Pageable pageable){
        Page<ProductResponseDTO> products = productService.findByProductsRange(min, max, pageable).map(this::toResponse);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<Page<ProductResponseDTO>> findLowOnStockProduct(@RequestParam int threshold, Pageable pageable){
        Page<ProductResponseDTO> products = productService.getLowOnStockProduct(threshold, pageable).map(this::toResponse);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<Page<ProductResponseDTO>> allProducts(Pageable pageable){
        Page<ProductResponseDTO> products = productService.allProducts(pageable).map(this::toResponse);
        return ResponseEntity.ok(products);
    }

    @PutMapping("{productId}")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID productId,
                                                            @RequestBody ProductDTO productDTO){
        Product product = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(toResponse(product));
    }

    @DeleteMapping("{productId}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId){
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    public ProductResponseDTO toResponse(Product product){
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
