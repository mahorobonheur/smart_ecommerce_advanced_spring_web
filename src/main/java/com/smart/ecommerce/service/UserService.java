package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.UserDTO;
import com.smart.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    User createUser(UserDTO dto);
    User getUserById(UUID userId);
    Page<User> getAllUsers(Pageable pageable);
    void deleteUser(UUID userId);
    User updateUser(UUID id, UserDTO userDetails);
    User findByEmail(String email);
}
