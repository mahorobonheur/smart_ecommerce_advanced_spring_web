package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.CategoryDTO;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Category;
import com.smart.ecommerce.repository.CategoryRepository;
import com.smart.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Profile("dev")
@Transactional(
        propagation = Propagation.REQUIRED,
        rollbackFor = { ResourceNotFoundException.class, IllegalArgumentException.class, RuntimeException.class }
)
public class CategoryServiceDevImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    @CacheEvict(value = "categoriesPage", allEntries = true)
    public Category addCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Cacheable(value = "categoryById", key = "#categoryId")
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Cacheable(value = "categoriesPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Category> allCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categoriesPage", allEntries = true),
            @CacheEvict(value = "categoryById", key = "#categoryId")
    })
    public Category updateCategory(UUID categoryId, CategoryDTO categoryDetails) {
        Category existingCategory = getCategoryById(categoryId);

        if (!existingCategory.getCategoryName().equals(categoryDetails.getCategoryName())
                && categoryRepository.existsByCategoryName(categoryDetails.getCategoryName())) {
            throw new IllegalArgumentException("This category already exists!");
        }

        existingCategory.setCategoryName(categoryDetails.getCategoryName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categoriesPage", allEntries = true),
            @CacheEvict(value = "categoryById", key = "#categoryId")
    })
    public void deleteCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category with id " + categoryId + " is not found");
        }
        categoryRepository.deleteById(categoryId);
    }
}
