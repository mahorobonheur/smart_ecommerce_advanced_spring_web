package com.smart.ecommerce.specifications;

import com.smart.ecommerce.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> searchUsers(String keyWord){
        return (root, query, cb) -> {

            String searchTerm = "%" + keyWord.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("email")), searchTerm),
                    cb.like(cb.lower(root.get("fullName")), searchTerm)
            );
        };
    }
}
