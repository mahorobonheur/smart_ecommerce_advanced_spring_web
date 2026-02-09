package com.smart.ecommerce.config;

import com.smart.ecommerce.service.CustomUserDetailService;
import com.smart.ecommerce.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final TokenService tokenService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailService customUserDetailService, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailService = customUserDetailService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                try {

                    if (header != null && header.startsWith("Bearer ")) {
                        String token = header.substring(7);

                        Claims claims = jwtUtil.extractClaims(token);
                        String email = claims.getSubject();
                        String role = claims.get("role", String.class);

                        if (tokenService.isTokenRevoked(token)) {
                            throw new JwtException("Token has been revoked");
                        }

                        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (JwtException ex){
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            """
                {
                  "error": "Invalid or expired JWT token"
                }
            """
                    );
                    return;
                }
                filterChain.doFilter(request, response);
    }
}
