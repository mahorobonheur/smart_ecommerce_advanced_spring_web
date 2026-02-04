package com.smart.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GlobalSearchResponseDTO {
    private List<UserResponseDTO> users;
    private List<ProductResponseDTO> products;
    private List<OrderResponseDTO> orders;
}
