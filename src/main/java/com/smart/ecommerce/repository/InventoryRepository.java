package com.smart.ecommerce.repository;

import com.smart.ecommerce.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
 boolean existsByProduct_ProductId(UUID productId);
 Inventory findByProduct_ProductId(UUID productId);
}
