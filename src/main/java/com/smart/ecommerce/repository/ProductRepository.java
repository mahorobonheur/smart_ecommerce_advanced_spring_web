package com.smart.ecommerce.repository;

import com.smart.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Page<Product> findByCategory_CategoryName(String categoryName, Pageable pageable);
    Page<Product> findByPriceBetween(double min, double max, Pageable pageable);
    @Query(
            "select p FROM Product p JOIN p.inventory i where i.quantityAvailable <:threshold order by i.quantityAvailable ASC"
    )
    Page<Product> findProductLowOnStock(@Param("threshold") int threshold, Pageable pageable);
}
