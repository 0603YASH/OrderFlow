package com.orderflow.user_service.service;

import com.orderflow.user_service.dto.request.UserRegistrationRequest;
import com.orderflow.user_service.dto.response.UserResponse;
import com.orderflow.user_service.entity.User;
import com.orderflow.user_service.enums.Role;
import com.orderflow.user_service.exception.UserAlreadyExistsException;
import com.orderflow.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_success_returnsUserResponse() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Yash");
        request.setEmail("yash@test.com");
        request.setPassword("Test@1234");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Yash");
        savedUser.setEmail("yash@test.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.USER);

        when(userRepository.existsByEmail("yash@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Test@1234")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("yash@test.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertEquals("Yash", response.getName());
    }

    @Test
    void registerUser_emailAlreadyExists_throwsException() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Yash");
        request.setEmail("yash@test.com");
        request.setPassword("Test@1234");

        when(userRepository.existsByEmail("yash@test.com")).thenReturn(true);

        // Act + Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
    }
}
