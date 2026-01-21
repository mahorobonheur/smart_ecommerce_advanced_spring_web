package com.smart.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDTO {
    @NotBlank(message = "Category name can not be blank.")
    public String categoryName;
}
