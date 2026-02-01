package com.smart.ecommerce.graphql;

import com.smart.ecommerce.dto.request.UserDTO;
import com.smart.ecommerce.dto.response.UserResponseDTO;
import com.smart.ecommerce.dto.graphql.UserPageDTO;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.service.UserService;
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
public class UserGraphQLController {

    @Autowired
    private UserService userService;

    @QueryMapping
    @Operation(summary = "GraphQL: Get user by Id ")
    public UserResponseDTO userById(@Argument String userId) {
        User user = userService.getUserById(UUID.fromString(userId));
        return toResponse(user);
    }

    @QueryMapping
    @Operation(summary = "GraphQL: Get All Users")
    public UserPageDTO allUsers(@Argument Integer page, @Argument Integer size) {
        Page<User> usersPage = userService.getAllUsers(
                PageRequest.of(page != null ? page : 0, size != null ? size : 10)
        );

        List<UserResponseDTO> users = usersPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new UserPageDTO(
                users,
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.getNumber(),
                usersPage.getSize()
        );
    }

    @MutationMapping
    @Operation(summary = "GraphQL: Create user")
    public UserResponseDTO createUser(@Argument UserDTO input) {
        return toResponse(userService.createUser(input));
    }

    @MutationMapping
    @Operation(summary = "GraphQL: Update user")
    public UserResponseDTO updateUser(@Argument String userId, @Argument UserDTO input) {
        return toResponse(userService.updateUser(UUID.fromString(userId), input));
    }

    @MutationMapping
    @Operation(summary = "GraphQL: Delete user")
    public Boolean deleteUser(@Argument String userId) {
        userService.deleteUser(UUID.fromString(userId));
        return true;
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
