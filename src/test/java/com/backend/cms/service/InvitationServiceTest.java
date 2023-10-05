package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class InvitationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private InvitationService invitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendInvitation_NewUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("newuser@example.com");
        request.setFirstName("Joe");
        request.setLastName("Doe");

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(null);

        // Act
        invitationService.sendInvitation(request);

        // Assert
        verify(javaMailSender).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void sendInvitation_ExistingUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Joe");
        request.setLastName("Doe");
        request.setEmail("alreadyexists@example.com");

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(new User());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            invitationService.sendInvitation(request);
        });

        assertEquals("Invitation already sent", exception.getMessage());
    }

    @Test
    void isTokenExpired_ExpiredToken() {
        InvitationService invitationService = new InvitationService();
        // Past date (e.g., 1696284000000 for October 2, 2023)
        String expiredToken = "a2a8bcaf7ee64dd38156a2fb3ea52e96_1696284000000";

        boolean result = invitationService.isTokenExpired(expiredToken);

        assertTrue(result);
    }

    @Test
    void isTokenExpired_ValidToken() {
        InvitationService invitationService = new InvitationService();

        // Future expiration time (e.g., 1727906400000 for October 3, 2024)
        String validToken = "a2a8bcaf7ee64dd38156a2fb3ea52e96_1727906400000";

        boolean result = invitationService.isTokenExpired(validToken);

        assertFalse(result);
    }


    @Test
    void generateUniqueToken() {
        InvitationService invitationService = new InvitationService();

        String token1 = invitationService.generateUniqueToken();
        String token2 = invitationService.generateUniqueToken();

        assertNotEquals(token1, token2);
    }

    @Test
    void resendInvitation_ExpiredToken() {
        // Arrange
        String userId = "someUserId";
        // Past date (e.g., 1696284000000 for October 2, 2023)
        String expiredToken = "a2a8bcaf7ee64dd38156a2fb3ea52e96_1696284000000";
        User userWithExpiredToken = new User();
        userWithExpiredToken.setToken(expiredToken);

        // Mock to return the user with an expired token
        when(userRepository.findByUserId(userId)).thenReturn(userWithExpiredToken);

        // Act
        boolean result = invitationService.resendInvitation(userId);

        // Assert
        // the invitation should be able to be sent since the token has expired
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(javaMailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }


    @Test
    void resendInvitation_ValidToken() {
        // Arrange
        String userId = "someUserId";
        // Future expiration time (e.g., 1727906400000 for October 3, 2024)
        String validToken = "a2a8bcaf7ee64dd38156a2fb3ea52e96_1727906400000";
        User userWithValidToken = new User();
        userWithValidToken.setToken(validToken);

        // Mock to return the user with a valid token
        when(userRepository.findByUserId(userId)).thenReturn(userWithValidToken);

        // Act
        boolean result = invitationService.resendInvitation(userId);

        // Assert
        // the invitation should not be sent since the token is still valid
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
        verify(javaMailSender, never()).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void resendInvitation_UserNotFound() {
        // Arrange
        String userId = "userNotFound";

        // Mock to return null (user not found)
        when(userRepository.findByUserId(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            invitationService.resendInvitation(userId);
        });
        verify(userRepository, never()).save(any(User.class));
        verify(javaMailSender, never()).send(Mockito.any(SimpleMailMessage.class));
    }

}
