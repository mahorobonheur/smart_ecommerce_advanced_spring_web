package com.smart.ecommerce.service.implementation.dev;

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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Profile("dev")
public class ProductServiceDevImplementation implements ProductService {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {
                    ResourceNotFoundException.class,
                    IllegalArgumentException.class
            }
    )
    @CacheEvict(value = "productsPage", allEntries = true)
    public Product addProduct(ProductDTO dto) {

        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCreatedAt(LocalDateTime.now());
        product.setCategory(
                categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"))
        );

        Product savedProduct = productRepository.save(product);

        InventoryDTO inventoryDTO =
                new InventoryDTO(savedProduct.getProductId(), savedProduct.getStock());

        Inventory inventory = inventoryService.createInventory(inventoryDTO);
        savedProduct.setInventory(inventory);

        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productById", key = "#productId")
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }



    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productsPage",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public Page<Product> allProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productsByCategory",
            key = "'category:' + #categoryName + '-page:' + #pageable.pageNumber + '-size:' + #pageable.pageSize + '-sort:' + #pageable.sort.toString()"
    )
    public Page<Product> findByCategory(String categoryName, Pageable pageable){
        return productRepository.findByCategory_CategoryName(categoryName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "lowStockProducts",
            key = "'threshold:' + #threshold + 'page:' + #pageable.pageNumber + '-size:' + #pageable.pageSize + '-sort:' + #pageable.sort.toString()"
    )
    public Page<Product> getLowOnStockProduct(int threshold, Pageable pageable){
        return productRepository.findProductLowOnStock(threshold, pageable);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "#productsInRange",
            key = "'min:' + #min + '-max:' + #max + '-page:' + #pageable.pageNumber + '-size:' + #pageable.pageSize + '-sort:' + #pageable.sort.toString()"
    )
    public Page<Product> findByProductsRange(double min, double max, Pageable pageable){
        return productRepository.findByPriceBetween(min, max, pageable);
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {
                    ResourceNotFoundException.class,
                    IllegalArgumentException.class
            }
    )
    @Caching(evict = {
            @CacheEvict(value = "productsPage", allEntries = true),
            @CacheEvict(value = "productById", key = "#productId")
    })
    public Product updateProduct(UUID productId, ProductDTO dto) {

        Product existingProduct = getProductById(productId);

        existingProduct.setProductName(dto.getProductName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());
        existingProduct.setCategory(
                categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"))
        );

        Product updatedProduct = productRepository.save(existingProduct);

        Inventory inventory =
                inventoryService.getInventoryByProductId(updatedProduct.getProductId());

        InventoryDTO inventoryDTO =
                new InventoryDTO(updatedProduct.getProductId(), updatedProduct.getStock());

        inventoryService.updateInventory(inventory.getInventoryId(), inventoryDTO);

        return updatedProduct;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "productsPage", allEntries = true),
            @CacheEvict(value = "productById", key = "#productId")
    })
    public void deleteProduct(UUID productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        Inventory inventory =
                inventoryService.getInventoryByProductId(productId);

        inventoryService.deleteInventory(inventory.getInventoryId());
        productRepository.deleteById(productId);
    }
}
