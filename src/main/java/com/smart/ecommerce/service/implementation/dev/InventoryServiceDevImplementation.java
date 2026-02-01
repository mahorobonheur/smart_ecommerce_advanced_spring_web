package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.InventoryDTO;
import com.smart.ecommerce.dto.response.InventoryResponseDTO;
import com.smart.ecommerce.exception.DuplicateResourceException;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Inventory;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.repository.InventoryRepository;
import com.smart.ecommerce.repository.ProductRepository;
import com.smart.ecommerce.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Profile("dev")
@Transactional
public class InventoryServiceDevImplementation implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    @CacheEvict(value = "inventoriesPage", allEntries = true)
    public Inventory createInventory(InventoryDTO dto) {
        if(inventoryRepository.existsByProduct_ProductId(dto.getProductId())){
            throw new DuplicateResourceException("This product already exists.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product does not exist!"));
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantityAvailable(dto.getQuantityAvailable());
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventoryById", key = "#inventoryId")
    public Inventory getInventoryById(UUID inventoryId) {
        return inventoryRepository.findById(inventoryId).orElseThrow(
                () -> new ResourceNotFoundException("Inventory not found!")
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventoryByProduct", key = "#productId")
    public Inventory getInventoryByProductId(UUID productId) {
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId);
        if(inventory == null){
            throw new ResourceNotFoundException("Inventory for this product not found!");
        }
        return inventory;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventoriesPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<InventoryResponseDTO> allInventories(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(
                inventory -> new InventoryResponseDTO(
                        inventory.getInventoryId(),
                        inventory.getProduct().getProductId(),
                        inventory.getProduct().getProductName(),
                        inventory.getQuantityAvailable(),
                        inventory.getLastUpdated()
                )
        );
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "inventoriesPage", allEntries = true),
            @CacheEvict(value = "inventoryById", key = "#inventoryId"),
            @CacheEvict(value = "inventoryByProduct", key = "#dto.productId")
    })
    public Inventory updateInventory(UUID inventoryId, InventoryDTO dto) {
        Inventory inventory = getInventoryById(inventoryId);
        if(!inventory.getProduct().getProductId().equals(dto.getProductId())){
            throw new IllegalArgumentException("Cannot change product of an existing inventory");
        }
        inventory.setQuantityAvailable(dto.getQuantityAvailable());
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "inventoriesPage", allEntries = true),
            @CacheEvict(value = "inventoryById", key = "#inventoryId"),
            @CacheEvict(value = "inventoryByProduct", key = "#inventoryRepository.findById(inventoryId).get().getProduct().getProductId()")
    })
    public void deleteInventory(UUID inventoryId) {
        inventoryRepository.deleteById(inventoryId);
    }
}
