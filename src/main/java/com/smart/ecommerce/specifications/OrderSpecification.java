package com.smart.ecommerce.specifications;

import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> searchOrders(String keyword) {
        return (root, query, cb) -> {
            String searchTerm = "%" + keyword.toLowerCase() + "%";

            Join<Order, User> userJoin = root.join("user", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("paymentIntentId")), searchTerm),
                    cb.like(cb.lower(userJoin.get("email")), searchTerm),
                    cb.like(cb.lower(userJoin.get("fullName")), searchTerm)
            );
        };
    }
}
