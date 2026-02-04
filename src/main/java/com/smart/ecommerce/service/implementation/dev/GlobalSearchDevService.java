package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.mapper.OrderMapper;
import com.smart.ecommerce.dto.mapper.ProductMapper;
import com.smart.ecommerce.dto.mapper.UserMapper;
import com.smart.ecommerce.dto.response.GlobalSearchResponseDTO;
import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.dto.response.ProductResponseDTO;
import com.smart.ecommerce.dto.response.UserResponseDTO;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.ProductRepository;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.GlobalSearchService;
import com.smart.ecommerce.specifications.OrderSpecification;
import com.smart.ecommerce.specifications.ProductSpecification;
import com.smart.ecommerce.specifications.UserSpecification;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Profile("dev")
public class GlobalSearchDevService implements GlobalSearchService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public GlobalSearchDevService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public GlobalSearchResponseDTO searchAll(String keyWord) {
        List<UserResponseDTO> users = userRepository
                .findAll(UserSpecification.searchUsers(keyWord))
                .stream()
                .map(UserMapper::toDto)
                .toList();

        List<OrderResponseDTO> orders = orderRepository
                .findAll(OrderSpecification.searchOrders(keyWord))
                .stream()
                .map(OrderMapper::toDto)
                .toList();

        List<ProductResponseDTO> products = productRepository
                .findAll(ProductSpecification.searchProduct(keyWord))
                .stream()
                .map(ProductMapper::toDto)
                .toList();

        return new GlobalSearchResponseDTO(users, products, orders);
    }
}
