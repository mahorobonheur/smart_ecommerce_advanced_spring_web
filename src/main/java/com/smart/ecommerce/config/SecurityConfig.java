package com.smart.ecommerce.config;

import com.smart.ecommerce.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
                csrf(
                csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST,
                                "/",
                                "/api/users",
                                "/api/users/login"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/products",
                                "/api/search/global",
                                "/api/reviews",
                                "/api/reviews/product/{productId}",
                                "/api/products/{productId}",
                                "/api/category/{categoryId}",
                                "/api/category/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/review",
                                "/api/products",
                                "/api/orders/checkout/{userId}",
                                "/api/orders/confirm",
                                "/api/category/",
                                "/api/cart/add").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users",
                                "/api/users/{userId}",
                                "/api/orders/{orderId}",
                                "/api/orders",
                                "/api/cart/{userId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}",
                                "/api/reviews/{reviewId}",
                                "/api/products/{productId}",
                                "/api/orders/{orderId}",
                                "/api/category/{categoryId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}",
                                "/api/reviews/{reviewId}",
                                "/api/products/{productId}",
                                "/api/orders/{orderId}",
                                "/api/category/{categoryId}",
                                "/api/cart/remove",
                                "/api/cart/clear/{userId}").authenticated()
                        .anyRequest().authenticated()

                )
                .sessionManagement(session -> session.sessionFixation().newSession());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5172"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
