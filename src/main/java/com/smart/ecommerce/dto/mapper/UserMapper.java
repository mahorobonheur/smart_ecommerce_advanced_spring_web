package com.smart.ecommerce.dto.mapper;

import com.smart.ecommerce.dto.response.UserResponseDTO;
import com.smart.ecommerce.model.User;

public class UserMapper {
    public static UserResponseDTO toDto(User user){
        return new UserResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                user.getCreatedAt()
        );
    }
}
