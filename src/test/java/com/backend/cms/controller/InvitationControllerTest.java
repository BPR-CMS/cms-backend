package com.backend.cms.controller;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreateUserRequest;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.backend.cms.service.InvitationService;
import com.backend.cms.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvitationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InvitationService invitationService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private InvitationController invitationController;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Generate a valid token
        String userId = "ubcy8c";
        token = jwtTokenUtil.generateToken(userId);
        // Defining the behavior to return a mockUser
        User mockUser = new User();
        when(userRepository.findByUserId(eq(userId))).thenReturn(mockUser);
    }

    @Test
    void testSendInvitation_ValidData() throws Exception {
        //  valid CreateUserRequest
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("loredanacicati@example.com");

        // Mocking the behavior of sendInvitation()
        doNothing().when(invitationService).sendInvitation(request);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);


        // Perform the POST request
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation sent successfully!"));
    }

    @Test
    void testSendInvitation_InvalidEmail() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        // invalid email address
        request.setEmail("invalidemail");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendInvitation_InvalidFirstName() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        // invalid first name
        request.setFirstName("J");
        request.setLastName("Doe");
        request.setEmail("loredanacicati@example.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendInvitation_InvalidLastName() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Joe");
        // invalid last name
        request.setLastName("D");
        request.setEmail("loredanacicati@example.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendInvitation_EmptyFields() throws Exception {

        CreateUserRequest request = new CreateUserRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResendInvitation_Success() throws Exception {
        // Define user ID
        String userId = "ubcy8c";

        // Mock the behavior to return true (successful resend)
        when(invitationService.resendInvitation(eq(userId))).thenReturn(true);

        // Perform the POST request to resend invitation
        mvc.perform(
                        post("/api/v1/invitations/resend/{userId}", userId)
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testResendInvitation_UserNotFound() throws Exception {
        // Define a non-existent user ID
        String userId = "nonexistent";

        when(invitationService.resendInvitation(eq(userId))).thenThrow(new NotFoundException());

        mvc.perform(
                        post("/api/v1/invitations/resend/{userId}", userId)
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMultipleInvitationsToSameEmailAddress() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("user@gmail.com");

        // Mocking the behavior of sendInvitation()
        doNothing().doThrow(new IllegalStateException("Invitation already sent")).when(invitationService).sendInvitation(request);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation sent successfully!"));

        // Sending the invitation again
        mvc.perform(
                        post("/api/v1/invitations/send")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invitation already sent"));

        verify(invitationService, times(2)).sendInvitation(request);
    }
}
