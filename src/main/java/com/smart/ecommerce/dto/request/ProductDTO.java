package com.smart.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "Product name can not be null")
    private String productName;
    @Min(0)
    private double price;
    @Min(0)
    private int stock;
    private UUID categoryId;

}
