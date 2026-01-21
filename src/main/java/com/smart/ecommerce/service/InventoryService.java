package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.InventoryDTO;
import com.smart.ecommerce.dto.response.InventoryResponseDTO;
import com.smart.ecommerce.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InventoryService {
    Inventory createInventory(InventoryDTO dto);
    Inventory getInventoryById(UUID inventoryId);
    Inventory getInventoryByProductId(UUID productId);
    Page<InventoryResponseDTO> allInventories(Pageable pageable);
    Inventory updateInventory(UUID inventoryId, InventoryDTO dto);
    void deleteInventory(UUID inventoryId);
}
