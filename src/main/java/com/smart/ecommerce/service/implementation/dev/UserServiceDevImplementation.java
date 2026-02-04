package com.smart.ecommerce.service.implementation.dev;

import com.smart.ecommerce.dto.request.UserDTO;
import com.smart.ecommerce.exception.DuplicateResourceException;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.model.Role;
import com.smart.ecommerce.model.User;
import com.smart.ecommerce.repository.UserRepository;
import com.smart.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Profile("dev")
public class UserServiceDevImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @CacheEvict(value = "usersPage", allEntries = true)
    public User createUser(UserDTO dto) {
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("User with this email already exists!");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setRole(Role.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }


    @Override
    @Cacheable(value = "userById", key = "#userId")
    public User getUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    @Override
    @Cacheable(value = "usersPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    @Override
    public User login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("Invalid credentials");
        }
         return user;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"userById", "usersPage"}, allEntries = true)
    public void deleteUser(UUID userId){
        if(!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User with Id " + userId + " is not found");
        }
        userRepository.deleteById(userId);
    }


    @Override
    @Transactional
    @CachePut(value = "userById", key = "#userId")
    @CacheEvict(value = "usersPage", allEntries = true)
    public User updateUser(UUID userId, UserDTO userDetails) {
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found!")
        );

        if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email " + userDetails.getEmail() + " is already taken by another user.");
        }

        existingUser.setFullName(userDetails.getFullName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(Role.valueOf(userDetails.getRole()));
        existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));


        return userRepository.save(existingUser);
    }
}
