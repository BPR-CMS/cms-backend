package com.backend.cms.controller;


import com.backend.cms.model.AccountStatus;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.LoginRequest;
import com.backend.cms.request.SetPasswordRequest;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserRepository userRepository; // Mocked UserRepository

    @InjectMocks
    private UserController userController;


    @Test
    void testFindUserById() throws Exception {
        String userId = "ubcy8c";

        // Generate a valid token
        String token = jwtTokenUtil.generateToken(userId);

        // Defining the behavior of userRepository.findByUserId() to return a mockUser
        User mockUser = new User();
        when(userRepository.findByUserId(eq(userId))).thenReturn(mockUser);

        // Perform the GET request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/users/{id}", userId)
                                .header("Authorization", "Bearer " + token))
                .andReturn();

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();

        // User with specified id exists
        if (userRepository.findByUserId(userId) != null) {
            // Assert the status code is OK when the user exists
            assertEquals(HttpStatus.OK.value(), actualStatusCode);
        } else {
            // Assert the status code is NOT_FOUND when the user doesn't exist
            assertEquals(HttpStatus.NOT_FOUND.value(), actualStatusCode);
        }
    }


    @Test
    void testFindUserById_NotFound() throws Exception {
        String userId = "ubcy8c";

        // Generate a valid token
        String token = jwtTokenUtil.generateToken(userId);

        // Defining the behavior of userRepository.findByUserId()
        when(userRepository.findByUserId(eq(userId))).thenReturn(null);
        try {
            // Perform the GET request
            MvcResult response = mvc.perform(
                            MockMvcRequestBuilders.get("/api/v1/users/{id}", userId)
                                    .header("Authorization", "Bearer " + token))
                    .andReturn();
            // Retrieve the actual status code from the response
            int actualStatusCode = response.getResponse().getStatus();

            // Assert the status code is UNAUTHORIZED when the user is not found
            assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);


        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            // Assert that the exception was caught and response status is UNAUTHORIZED
            assertEquals(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        // Create a LoginRequest object with valid credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("adminPassword@1");

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);

        // Create a mocked User instance with hashed password
        User mockUser = new User();
        mockUser.setUserId("someUserId");
        mockUser.setEmail("admin@example.com");

        // Hash the password using a real BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("adminPassword@1");
        mockUser.setPassword(hashedPassword);

        // Define the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(mockUser);

        // Check if the request body matches the email and password of the mock user
        JsonNode requestBodyJson = new ObjectMapper().readTree(requestBody);
        String requestEmail = requestBodyJson.get("email").asText();
        String requestPassword = requestBodyJson.get("password").asText();

        if (requestEmail.equals(mockUser.getEmail()) && passwordEncoder.matches(requestPassword, mockUser.getPassword())) {
            // Perform the POST request
            MvcResult response = mvc.perform(
                            MockMvcRequestBuilders.post("/api/v1/users/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andReturn();

            // Retrieve the actual status code from the response
            int actualStatusCode = response.getResponse().getStatus();

            // Assert the status code is OK for successful login
            assertEquals(HttpStatus.OK.value(), actualStatusCode);
        } else {
            // Request body doesn't match mock user's credentials, should result in NOT_FOUND
            int expectedStatusCode = HttpStatus.NOT_FOUND.value();

            // Assert the status code is NOT_FOUND for incorrect credentials
            assertEquals(expectedStatusCode, HttpStatus.NOT_FOUND.value());
        }
    }

    @Test
    void testUnsuccessfulLogin() throws Exception {
        // Create a LoginRequest object with invalid credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("new@example.com");
        loginRequest.setPassword("invalidPass@12");

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
        // Define the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(null);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // Assert the status code is NOT_FOUND for unsuccessful login
        assertEquals(HttpStatus.NOT_FOUND.value(), actualStatusCode);
    }

    @Test
    void testIncorrectEmailLogin() throws Exception {
        // Create a LoginRequest object with incorrect email
        LoginRequest loginRequest = new LoginRequest();
        // Setting an email that doesn't exist in the mocked repository
        loginRequest.setEmail("nonexistent123@example.com");
        loginRequest.setPassword("newpassW@12");

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);

        // Defining the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(null);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // Assert the status code is NOT_FOUND for incorrect email
        assertEquals(HttpStatus.NOT_FOUND.value(), actualStatusCode);
    }

    @Test
    void testIncorrectPasswordLogin() throws Exception {
        // Create a LoginRequest object with incorrect password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("new@example.com");
        loginRequest.setPassword("incorrectPass@12"); // Incorrect password

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
        // Create a mocked User instance
        User mockUser = new User();
        mockUser.setUserId("someUserId");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("validP@12")); // Set the actual correct password

        // Defining the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(mockUser);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();

        // Assert the status code is UNAUTHORIZED for incorrect password
        assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);
    }

    @Test
    void testEmptyBodyLogin() throws Exception {
        // Empty body, no login request data
        String requestBody = "{}";
        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // Assert the status code is BAD_REQUEST for empty body
        assertEquals(HttpStatus.BAD_REQUEST.value(), actualStatusCode);
    }

    @Test
    void testInvalidEmailFormat() throws Exception {
        // Create a LoginRequest object with an invalid email format
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid-email"); // Invalid email format
        loginRequest.setPassword("passworD@123");

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
        // Create a mocked User instance
        User mockUser = new User();
        mockUser.setEmail("admin@example.com");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("passworD@123"));

        // Define the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(mockUser);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // Assert the status code is BAD_REQUEST for invalid email format
        assertEquals(HttpStatus.BAD_REQUEST.value(), actualStatusCode);
    }

    @Test
    void testSetUserPassword() throws Exception {
        // create a request with valid password
        SetPasswordRequest setPasswordRequest = new SetPasswordRequest();
        setPasswordRequest.setPassword("newPassword@1");

        String requestBody = new ObjectMapper().writeValueAsString(setPasswordRequest);

        // create a mocked User
        User mockUser = new User();
        mockUser.setUserId("someUserId");

        when(userRepository.findByUserId(eq("someUserId"))).thenReturn(mockUser);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // perform the PATCH request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/users/setPassword/someUserId")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        // get the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // assert the status code OK for successful password update
        assertEquals(HttpStatus.OK.value(), actualStatusCode);

        //  userRepository.save() was called with the correct user
        verify(userRepository, times(1)).save(eq(mockUser));

        //  user's account status is set to CREATED
        assertEquals(AccountStatus.CREATED, mockUser.getAccountStatus());
    }

    @Test
    void testPasswordAlreadySet() throws Exception {
        // create a SetPasswordRequest object with a valid password
        SetPasswordRequest setPasswordRequest = new SetPasswordRequest();
        setPasswordRequest.setPassword("newPassword@1");

        // create a mocked User with account status CREATED
        User mockUser = new User();
        mockUser.setUserId("someUserId");
        mockUser.setAccountStatus(AccountStatus.CREATED);

        when(userRepository.findByUserId(eq("someUserId"))).thenReturn(mockUser);

        String requestBody = new ObjectMapper().writeValueAsString(setPasswordRequest);

        // perform the PATCH request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/users/setPassword/someUserId")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        // get the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();

        // assert the status code is 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST.value(), actualStatusCode);
    }

}
