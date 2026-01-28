package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.CategoryDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Category;
import com.smart.ecommerce.repository.CategoryRepository;
import com.smart.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Profile("dev")
public class CategoryServiceDevImplementation implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Category addCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category not found."));
    }

    @Override
    public Page<Category> allCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category updateCategory(UUID categoryId, CategoryDTO categoryDetails) {
        Category existingCategory = getCategoryById(categoryId);

        if(!existingCategory.getCategoryName().equals(categoryDetails.getCategoryName())
        && categoryRepository.existsByCategoryName(categoryDetails.getCategoryName())){
            throw new IllegalArgumentException("This category already exists!");
        }
        existingCategory.setCategoryName(categoryDetails.getCategoryName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
     if(!categoryRepository.existsById(categoryId)){
         throw new ResourceNotFoundException("Category with id " + categoryId + " is not found");
     }
     categoryRepository.deleteById(categoryId);
    }
}
