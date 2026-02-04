package com.smart.ecommerce.controller.rest;

import com.smart.ecommerce.config.JwtUtil;
import com.smart.ecommerce.dto.request.LoginRequestDto;
import com.smart.ecommerce.dto.request.UserDTO;
import com.smart.ecommerce.dto.response.JwtResponse;
import com.smart.ecommerce.dto.response.UserResponseDTO;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserService userService;

    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponseDTO> saveUser(
            @Parameter(description = "user info DTO") @Valid @RequestBody UserDTO dto){
        User user = userService.createUser(dto);
        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login API")
    public JwtResponse login(@RequestBody LoginRequestDto loginRequestDto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        User user = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        String token = jwtUtil.generateToken(user);

        return new JwtResponse(token, "Bearer", user.getEmail(), user.getRole().name());

    }


    @GetMapping("{userId}")
    @Operation(summary = "Get user by Id")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID userId){
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping
    @Operation(summary = "Get All users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable){
        Page<UserResponseDTO> users = userService.getAllUsers(pageable).map(this::toResponse);
        return ResponseEntity.ok(users);
    }

    @PutMapping("{userId}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userId,
                                           @Valid @RequestBody UserDTO userDTO){
        User updateUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(toResponse(updateUser));

    }

    @DeleteMapping("{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteMapping(@PathVariable UUID userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
