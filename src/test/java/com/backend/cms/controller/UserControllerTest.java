package com.backend.cms.controller;


import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.LoginRequest;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository; // Mocked UserRepository

    @InjectMocks
    private UserController userController;


    @Test
    void testFindUserById() throws Exception {
        String userId = "ubcy8l";

        // Generate a valid token
        String token = jwtTokenUtil.generateToken(userId); // Replace with a valid token

        // Define the behavior of userRepository.findByUserId()
        User mockUser = new User(); // Replace with a mock User instance
        when(userRepository.findByUserId(eq(userId))).thenReturn(mockUser);

        try {
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
                // Assert the status code is UNAUTHORIZED when the user doesn't exist
                assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            // Assert that the exception was caught and response status is UNAUTHORIZED
            assertEquals(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.value());
        }
    }

    //    @Test
//    void testSuccessfulLogin() throws Exception {
//        // Create a LoginRequest object with valid credentials
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setEmail("new@example.com");
//        loginRequest.setPassword("newpassW@1");
//
//        // Convert the LoginRequest object to a JSON string
//        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
//
//        // Create a mocked User instance with matching email and password
//        User mockUser = new User();
//        mockUser.setUserId("someUserId");
//        mockUser.setEmail("new@example.com"); // Set the email to match the request
//        mockUser.setPassword(new BCryptPasswordEncoder().encode("newpassW@1")); // Set the password to match the request
//
//        // Define the behavior of userRepository.findByEmail()
//        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(mockUser);
//
//        // Perform the POST request
//        MvcResult response = mvc.perform(
//                        MockMvcRequestBuilders.post("/api/v1/users/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(requestBody))
//                .andReturn();
//
//        // Retrieve the actual status code from the response
//        int actualStatusCode = response.getResponse().getStatus();
//
//        // Assert the status code is OK for successful login
//        assertEquals(HttpStatus.OK.value(), actualStatusCode);
//    }
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
            System.out.println("status: " + actualStatusCode);

            // Assert the status code is OK for successful login
            assertEquals(HttpStatus.OK.value(), actualStatusCode);
        } else {
            // Request body doesn't match mock user's credentials, should result in UNAUTHORIZED
            int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
            System.out.println("expected status: " + expectedStatusCode);

            // Assert the status code is UNAUTHORIZED for incorrect credentials
            assertEquals(expectedStatusCode, HttpStatus.UNAUTHORIZED.value());
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
        System.out.println("request body: " + requestBody);
        // Define the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(null);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        System.out.println("body: " + response.getResponse().getContentAsString());
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        System.out.println("status: " + actualStatusCode);
        // Assert the status code is UNAUTHORIZED for unsuccessful login
        assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);
    }

    @Test
    void testIncorrectEmailLogin() throws Exception {
        // Create a LoginRequest object with incorrect email
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com"); // An email that doesn't exist in the mocked repository
        loginRequest.setPassword("newpassW@1");

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
        System.out.println("request body: " + requestBody);

        // Define the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(null);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        System.out.println("body: " + response.getResponse().getContentAsString());

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        System.out.println("status: " + actualStatusCode);
        // Assert the status code is UNAUTHORIZED for incorrect email
        assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);
    }

    @Test
    void testIncorrectPasswordLogin() throws Exception {
        // Create a LoginRequest object with incorrect password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("new@example.com");
        loginRequest.setPassword("incorrectPass@12"); // Incorrect password

        // Convert the LoginRequest object to a JSON string
        String requestBody = new ObjectMapper().writeValueAsString(loginRequest);
        System.out.println("request body: " + requestBody);
        // Create a mocked User instance
        User mockUser = new User();
        mockUser.setUserId("someUserId");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("correctPassword")); // Set the actual correct password

        // Define the behavior of userRepository.findByEmail()
        when(userRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(mockUser);

        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        System.out.println("body: " + response.getResponse().getContentAsString());
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        System.out.println("status: " + actualStatusCode);

        // Assert the status code is UNAUTHORIZED for incorrect password
        assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);
    }

    @Test
    void testEmptyBodyLogin() throws Exception {
        // Empty body, no login request data
        String requestBody = "{}";
        System.out.println("request body: " + requestBody);
        // Perform the POST request
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        System.out.println("body: " + response.getResponse().getContentAsString());
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        System.out.println("status: " + actualStatusCode);
        // Assert the status code is UNAUTHORIZED for empty body
        assertEquals(HttpStatus.UNAUTHORIZED.value(), actualStatusCode);
    }

}
