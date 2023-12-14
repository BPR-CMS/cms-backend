package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testEncryptPassword() {
        // Trying any string password and the expected hashed password
        String anyStringPassword = "password";
        String expectedHashedPassword = "hashedPassword";

        // Stub the passwordEncoder.encode method to return the expected hashed password
        when(passwordEncoder.encode(anyStringPassword)).thenReturn(expectedHashedPassword);

        // When encrypting the password
        String hashedPassword = userService.encryptPassword(anyStringPassword);

        // Testing that the hashed password matches the expected hashed password
        assertEquals(expectedHashedPassword, hashedPassword);
    }

    @Test
    void testFindUserFailIfNotFound_UserFound() {
        User user = new User();
        when(userRepository.findByUserId("userId")).thenReturn(user);

        User result = userService.findUserFailIfNotFound("userId");

        assertEquals(user, result);
    }

    @Test
    void testFindUserFailIfNotFound_UserNotFound() {
        when(userRepository.findByUserId("userId")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            userService.findUserFailIfNotFound("userId");
        });
    }

    @Test
    void testFindNewId() {
        when(userRepository.findByUserId(anyString())).thenReturn(null);

        String result = userService.findNewId();

        assertNotNull(result);
        assertTrue(result.startsWith("u"));
    }

    @Test
    void testSave() {
        User user = new User();

        userService.save(user);

        verify(userRepository, times(1)).save(user);
    }


}
