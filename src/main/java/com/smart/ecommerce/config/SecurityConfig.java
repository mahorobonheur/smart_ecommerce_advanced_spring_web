package com.smart.ecommerce.config;

import com.smart.ecommerce.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
                csrf(
                csrf -> csrf.ignoringRequestMatchers("/api/**", "/graphql"))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/graphiql").permitAll()
                        .requestMatchers(HttpMethod.POST, "/graphql").permitAll()
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
                                "/api/products/by-category/{categoryName}",
                                "/api/products/between",
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
                                "/api/products/low-stock",
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
                .httpBasic(httpSecurityHttpBasicConfigurer -> {})

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5172"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception{
        return configuration.getAuthenticationManager();
    }
}
