package com.smart.ecommerce.service.implementation.prod;

import com.smart.ecommerce.dto.request.UserDTO;
import com.smart.ecommerce.exception.DuplicateResourceException;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Role;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Profile("prod")
public class UserServiceProd implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public User createUser(UserDTO dto) {
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("User with this email already exists!");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setRole(Role.valueOf(dto.getRole()));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    @Override
    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    @Override
    public User login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        return user;
    }
    @Override
    @Transactional
    public void deleteUser(UUID userId){
        if(!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User with Id " + userId + " is not found");
        }
        userRepository.deleteById(userId);
    }
    @Override
    @Transactional
    public User updateUser(UUID id, UserDTO userDetails) {
        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email " + userDetails.getEmail() + " is already taken by another user.");
        }

        existingUser.setFullName(userDetails.getFullName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(Role.valueOf(userDetails.getRole()));


        return userRepository.save(existingUser);
    }
}
