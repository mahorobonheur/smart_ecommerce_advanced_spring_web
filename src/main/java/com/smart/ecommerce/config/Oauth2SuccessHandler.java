package com.smart.ecommerce.config;

import com.smart.ecommerce.model.Role;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public Oauth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");


        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setFullName(fullName);
                    u.setPassword("OAUTH2_USER");
                    u.setRole(Role.CUSTOMER);
                    u.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(u);
                });

                 String token = jwtUtil.generateToken(user);
                 response.sendRedirect( "http://localhost:5172/oauth2/success?token=" + token);
           }

}
