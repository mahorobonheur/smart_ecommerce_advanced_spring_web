package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.mapper.OrderMapper;
import com.smart.ecommerce.dto.mapper.ProductMapper;
import com.smart.ecommerce.dto.mapper.UserMapper;
import com.smart.ecommerce.dto.response.GlobalSearchResponseDTO;
import com.smart.ecommerce.dto.response.OrderResponseDTO;
import com.smart.ecommerce.dto.response.ProductResponseDTO;
import com.smart.ecommerce.dto.response.UserResponseDTO;
import com.smart.ecommerce.model.Order;
import com.smart.ecommerce.model.Product;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.ProductRepository;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.GlobalSearchService;
import com.smart.ecommerce.specifications.OrderSpecification;
import com.smart.ecommerce.specifications.ProductSpecification;
import com.smart.ecommerce.specifications.UserSpecification;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Profile("dev")
@Transactional(
        readOnly = true
)
public class GlobalSearchDevService implements GlobalSearchService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final Executor asyncExecutor;

    public GlobalSearchDevService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository, Executor asyncExecutor) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.asyncExecutor = asyncExecutor;
    }

    @Override

    @Transactional(readOnly = true)
    public GlobalSearchResponseDTO searchAll(String keyWord) {


        List<User> users = userRepository.findAll(UserSpecification.searchUsers(keyWord));
        List<Order> orders = orderRepository.findAll(OrderSpecification.searchOrders(keyWord));
        List<Product> products = productRepository.findAll(ProductSpecification.searchProduct(keyWord));


        CompletableFuture<List<UserResponseDTO>> usersFuture = CompletableFuture.supplyAsync(
                () -> users.stream().map(UserMapper::toDto).toList(),
                asyncExecutor
        );

        CompletableFuture<List<OrderResponseDTO>> ordersFuture = CompletableFuture.supplyAsync(
                () -> orders.stream().map(OrderMapper::toDto).toList(),
                asyncExecutor
        );

        CompletableFuture<List<ProductResponseDTO>> productsFuture = CompletableFuture.supplyAsync(
                () -> products.stream().map(ProductMapper::toDto).toList(),
                asyncExecutor
        );

        CompletableFuture.allOf(usersFuture, ordersFuture, productsFuture).join();

        return new GlobalSearchResponseDTO(
                usersFuture.join(),
                productsFuture.join(),
                ordersFuture.join()
        );
    }

}
