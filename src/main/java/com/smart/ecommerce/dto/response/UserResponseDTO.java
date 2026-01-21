package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private UUID userId;
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;
}


