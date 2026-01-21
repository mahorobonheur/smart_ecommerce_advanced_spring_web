package com.smart.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    @NotBlank(message = "Full name is required!")
    private String fullName;

    @NotBlank(message = "Email is required!")
    @Email(message = "Email should be valid!")
    private String email;

    @NotBlank(message = "Password can not be empty")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;

    private String role;
}
