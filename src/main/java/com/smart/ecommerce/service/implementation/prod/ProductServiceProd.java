package com.smart.ecommerce.service.implementation.prod;

import com.smart.ecommerce.dto.request.InventoryDTO;
import com.smart.ecommerce.dto.request.ProductDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Inventory;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.repository.CategoryRepository;
import com.smart.ecommerce.repository.ProductRepository;
import com.smart.ecommerce.service.InventoryService;
import com.smart.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Profile("prod")
public class ProductServiceProd implements ProductService {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product addProduct(ProductDTO dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCreatedAt(LocalDateTime.now());
        product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        ));

        Product savedProduct = productRepository.save(product);
        InventoryDTO inventoryDTO = new InventoryDTO(savedProduct.getProductId(), savedProduct.getStock());
        Inventory inventory = inventoryService.createInventory(inventoryDTO);
        savedProduct.setInventory(inventory);
        return savedProduct;
    }

    @Override
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product not found")
        );
    }

    @Override
    @Cacheable
    public Page<Product> findByCategory(String categoryName, Pageable pageable){
        return null;
    }

    public Page<Product> getLowOnStockProduct(int threshold, Pageable pageable){
        return null;
    }

    @Override
    public Page<Product> findByProductsRange(double min, double max, Pageable pageable){
        return null;
    }

    @Override
    public Page<Product> allProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product updateProduct(UUID productId, ProductDTO dto) {
        Product existingProduct = getProductById(productId);
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setProductName(dto.getProductName());
        existingProduct.setStock(dto.getStock());
        existingProduct.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        ));

        Product updatedProduct = productRepository.save(existingProduct);
        InventoryDTO inventoryDTO = new InventoryDTO(updatedProduct.getProductId(), updatedProduct.getStock());
        Inventory inventory = inventoryService.getInventoryByProductId(updatedProduct.getProductId());
        inventoryService.updateInventory(inventory.getInventoryId(), inventoryDTO);

        return updatedProduct;
    }

    @Override
    public void deleteProduct(UUID productId) {
        if(!productRepository.existsById(productId)){
            throw new ResourceNotFoundException("Product not found.");
        }
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        inventoryService.deleteInventory(inventory.getInventoryId());
        productRepository.deleteById(productId);

    }
}
