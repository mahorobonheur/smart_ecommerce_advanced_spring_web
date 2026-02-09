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
    @Operation(summary = "Adding product api",
    description = "This can be accessed by only authenticated users")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductDTO productDTO){
        Product product = productService.addProduct(productDTO);
        return ResponseEntity.ok(toResponse(product));
    }

    @GetMapping("{productId}")
    @Operation(summary = "Get product by id",
    description = "This can be accessed by everyone, whether authenticated or not. You can be able to view the product")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(toResponse(product));
    }

    @GetMapping("/by-category/{categoryName}")
    @Operation(summary = "Get products by category name",
    description = "This helps to browse products by their category name, this is publicly available api")
    public ResponseEntity<Page<ProductResponseDTO>> getByCategoryName(@PathVariable String categoryName, Pageable pageable){
        Page<ProductResponseDTO> products = productService.findByCategory(categoryName, pageable).map(this::toResponse);
        return ResponseEntity.ok(products);

    }

    @GetMapping("/between")
    @Operation(summary = "Get products by price range",
    description = "The end point is accessible to all, to find the products they can afford in certain range")
    public ResponseEntity <Page<ProductResponseDTO>> findByPriceRange(@RequestParam double min, @RequestParam double max, Pageable pageable){
        Page<ProductResponseDTO> products = productService.findByProductsRange(min, max, pageable).map(this::toResponse);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Low stock products",
    description = "This is accessible to only admins, they can view low stock products for statistics")
    public ResponseEntity<Page<ProductResponseDTO>> findLowOnStockProduct(@RequestParam int threshold, Pageable pageable){
        Page<ProductResponseDTO> products = productService.getLowOnStockProduct(threshold, pageable).map(this::toResponse);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    @Operation(summary = "Get all products",
    description = "All products can be viewed in the market using this api, and this is publicly accessible")
    public ResponseEntity<Page<ProductResponseDTO>> allProducts(Pageable pageable){
        Page<ProductResponseDTO> products = productService.allProducts(pageable).map(this::toResponse);
        return ResponseEntity.ok(products);
    }

    @PutMapping("{productId}")
    @Operation(summary = "Update product",
    description = "Here you update product and this requires authentication and it is done only by CUSTOMERS")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID productId,
                                                            @RequestBody ProductDTO productDTO){
        Product product = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(toResponse(product));
    }

    @DeleteMapping("{productId}")
    @Operation(summary = "Delete product",
    description = "Here to delete product requires authentication, an admin or a customer can delete a product")
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
