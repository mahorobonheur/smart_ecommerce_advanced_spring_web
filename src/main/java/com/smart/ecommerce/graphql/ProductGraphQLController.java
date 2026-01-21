package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.graphql.ProductPageDTO;
import com.smart.ecommerce.dto.request.ProductDTO;
import com.smart.ecommerce.dto.response.ProductResponseDTO;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.service.ProductService;
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
public class ProductGraphQLController {
    @Autowired
    private ProductService productService;

    @QueryMapping
    public ProductResponseDTO productById(@Argument String productId){
        Product product = productService.getProductById(UUID.fromString(productId));
        return toResponse(product);
    }

    @QueryMapping
    public ProductPageDTO allProducts(@Argument Integer page, @Argument Integer size){
        Page<Product> productsPage = productService.allProducts(
                PageRequest.of(page != null ? page : 0, size != null ? size : 10));
        List<ProductResponseDTO> products = productsPage.getContent()
                .stream().map(this::toResponse).collect(Collectors.toList());

        return new ProductPageDTO(
                products,
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.getNumber(),
                productsPage.getSize()
        );
    }

    @MutationMapping
    public ProductResponseDTO createProduct(@Argument ProductDTO input){
        return toResponse(productService.addProduct(input));
    }

    @MutationMapping
    public ProductResponseDTO updateProduct(@Argument String productId, @Argument ProductDTO input){
        return toResponse(productService.updateProduct(UUID.fromString(productId), input));
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument String productId){
        productService.deleteProduct(UUID.fromString(productId));
        return true;
    }

    public ProductResponseDTO toResponse(Product product){
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getStock(),
                product.getInventory().getInventoryId(),
                product.getCategory().getCategoryId(),
                product.getCreatedAt()
        );
    }
}
