package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.dto.request.CategoryDTO;
import com.smart.ecommerce.dto.response.CategoryResponseDTO;
import com.smart.ecommerce.model.Category;
import com.smart.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/category/")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Adding category",
    description = "This api is only accessible by authorized users")
    public ResponseEntity<CategoryResponseDTO> addCategory(@RequestBody CategoryDTO dto){
        Category category = categoryService.addCategory(dto);
        return ResponseEntity.ok(toResponse(category));
    }

    @GetMapping("{categoryId}")
    @Operation(summary = "Getting category by Id",
    description = "Accessible at all users")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID categoryId){
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(toResponse(category));
    }

    @GetMapping
    @Operation(summary = "Get all categories",
    description = "Here this API is for every one, no authentication required")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(Pageable pageable){
        Page<CategoryResponseDTO> category = categoryService.allCategories(pageable).map(this::toResponse);
        return ResponseEntity.ok(category);
    }


    @PutMapping("{categoryId}")
    @Operation(summary = "Update category",
    description = "Update category only accessible by admins")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID categoryId,
                                                              @RequestBody CategoryDTO categoryDTO){
        Category category = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(toResponse(category));
    }

    @DeleteMapping("{categoryId}")
    @Operation(summary = "Delete category",
    description = "Only accessible to admins")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    public CategoryResponseDTO toResponse(Category category){
        return new CategoryResponseDTO(category.getCategoryId(), category.getCategoryName());
    }
}
