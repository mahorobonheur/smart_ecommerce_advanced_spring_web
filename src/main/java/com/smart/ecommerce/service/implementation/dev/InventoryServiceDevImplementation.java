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
    public Inventory getInventoryById(UUID inventoryId) {
        return inventoryRepository.findById(inventoryId).orElseThrow(
                () -> new ResourceNotFoundException("Inventory not found!")
        );
    }

    @Override
    public Inventory getInventoryByProductId(UUID productId) {
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId);
        if(inventory == null){
            throw new ResourceNotFoundException("Inventory for this product not found!");
        }
        return inventory;
    }

    @Override
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
    public Inventory updateInventory(UUID inventoryId, InventoryDTO dto) {
        Inventory inventory = getInventoryById(inventoryId);
        if(!inventory.getProduct().getProductId().equals(dto.getProductId())){
            throw new IllegalArgumentException("Cannot change product of an existing inventory");
        }
        inventory.setQuantityAvailable(dto.getQuantityAvailable());
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteInventory(UUID inventoryId) {
     inventoryRepository.deleteById(inventoryId);
    }
}
