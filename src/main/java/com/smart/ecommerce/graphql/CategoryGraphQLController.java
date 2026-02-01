package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.graphql.CategoryPageDTO;
import com.smart.ecommerce.dto.request.CategoryDTO;
import com.smart.ecommerce.dto.response.CategoryResponseDTO;
import com.smart.ecommerce.model.Category;
import com.smart.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class CategoryGraphQLController {

    @Autowired
    private CategoryService categoryService;

    @QueryMapping
    @Operation(summary = "GraphL: Get category by Id")
    public CategoryResponseDTO categoryById(@Argument String categoryId){
        Category category = categoryService.getCategoryById(UUID.fromString(categoryId));
        return toResponse(category);
    }

    @QueryMapping
    @Operation(summary = "GraphL: Get all categories")
    public CategoryPageDTO allCategories(@Argument Integer page, @Argument Integer size){
        Page<Category> categoryPage = categoryService.allCategories(
                PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        );

        List<CategoryResponseDTO> categoryResponseDTOS = categoryPage.map(this::toResponse)
                .stream()
                .collect(Collectors.toList());

        return new CategoryPageDTO(
                categoryResponseDTOS,
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.getNumber(),
                categoryPage.getSize());
    }

    @MutationMapping
    @Operation(summary = "GraphL: Create category")
    public CategoryResponseDTO createCategory(@Argument CategoryDTO input) {
        Category category = categoryService.addCategory(input);
        return toResponse(category);
    }

    @MutationMapping
    @Operation(summary = "GraphL: Update Category")
    public CategoryResponseDTO updateCategory(@Argument String categoryId, @Argument CategoryDTO input){
        return toResponse(categoryService.updateCategory(UUID.fromString(categoryId), input));
    }

    @MutationMapping
    @Operation(summary = "GraphL: Delete Category")
    public Boolean deleteCategory(@Argument String categoryId){
        categoryService.deleteCategory(UUID.fromString(categoryId));
        return true;
    }

    private CategoryResponseDTO toResponse(Category category){
        return new CategoryResponseDTO(category.getCategoryId(), category.getCategoryName());
    }
}
