package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.request.CategoryDTO;
import com.smart.ecommerce.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    Category addCategory(CategoryDTO dto);
    Category getCategoryById(UUID categoryId);
    Page<Category> allCategories(Pageable pageable);
    Category updateCategory(UUID categoryId, CategoryDTO categoryDetails);
    void deleteCategory(UUID categoryId);
}
