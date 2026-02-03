package com.smart.ecommerce.specifications;

import com.smart.ecommerce.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<Product> searchProduct(String keyWord){
        return (root, query, cb) -> {
          String searchTerm = "%" + keyWord.toLowerCase() + "%";
          return cb.or(
                  cb.like(cb.lower(root.get("productName")), searchTerm),
                  cb.like(cb.lower(root.join("category").get("categoryName")), searchTerm)
          );
        };
    }
}
