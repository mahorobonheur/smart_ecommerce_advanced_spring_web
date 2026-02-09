package com.smart.ecommerce.config;

import com.smart.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Oauth2SuccessHandler oauth2SuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, Oauth2SuccessHandler oauth2SuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
                csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/graphiql").hasRole("ADMIN")
                        .requestMatchers( "/oauth2/**",
                                "/login/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/graphql").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/",
                                "/api/users",
                                "/api/users/login"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/users",
                                "/api/users/{userId}",
                                "/api/products",
                                "/api/search/global",
                                "/api/reviews",
                                "/api/reviews/product/{productId}",
                                "/api/products/{productId}",
                                "/api/products/by-category/{categoryName}",
                                "/api/products/between",
                                "/api/category/{categoryId}",
                                "/api/category/").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/products",
                                "/api/orders/checkout/{userId}",
                                "/api/orders/confirm",
                                "/api/cart/add").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/review").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/category/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/products/low-stock",
                                "/api/orders/{orderId}",
                                "/api/orders",
                                "/api/cart/{userId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}",
                                "/api/reviews/{reviewId}",
                                "/api/products/{productId}",
                                "/api/orders/{orderId}").authenticated()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/reviews/{reviewId}",
                                "/api/products/{productId}").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/category/{categoryId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}",
                                "/api/reviews/{reviewId}",
                                "/api/products/{productId}",
                                "/api/orders/{orderId}",
                                "/api/cart/remove",
                                "/api/cart/clear/{userId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/category/{categoryId}").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .oauth2Login(oauth2 -> oauth2
                                .successHandler(oauth2SuccessHandler)
                        )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, autheException) ->
                                {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                                }

                                )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                {
                                    response.setStatus(403);
                                    response.getWriter().write("Forbidden");
                                }
                                ))
                .httpBasic(httpSecurityHttpBasicConfigurer -> {});


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:5172"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
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
