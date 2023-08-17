package com.backend.cms.controller;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreateInitAdminRequest;
import com.backend.cms.service.AdminInitializationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testCreateAdmin() throws Exception {
        // Mock the behavior of adminInitializationService.isAdminInitialized()
        AdminInitializationService adminInitializationService = Mockito.mock(AdminInitializationService.class);
        Mockito.when(adminInitializationService.isAdminInitialized()).thenReturn(false);

        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Set properties for the request object
        request.setFirstName("admin");
        request.setLastName("Loredana");
        request.setEmail("admin@gmail.com");
        // Encode the password using the passwordEncoder
        String expectedHash = passwordEncoder.encode("adminPassword");
        // Truncate the encoded password to the maximum allowed length (16 characters)
        if (expectedHash.length() > 16) {
            expectedHash = expectedHash.substring(0, 16);
        }
        request.setPassword(expectedHash);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request and get the result
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/initialize")
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        if (actualStatusCode == HttpStatus.CREATED.value()) {

            // Assert the status code
            assertEquals(HttpStatus.CREATED.value(), response.getResponse().getStatus());

            // If admin was successfully created, assert other parts of the response
            String json = response.getResponse().getContentAsString();
            UserDTO result = objectMapper.readValue(json, UserDTO.class);

            assertNotNull(result.getUserId());
            assertEquals("admin", result.getFirstName());
            assertEquals("Loredana", result.getLastName());
            assertEquals("admin@gmail.com", result.getEmail());

            // Assert the hashed password
            // Verify password match
            assertTrue(passwordEncoder.matches(expectedHash, result.getPassword()));
        } else { // Assert the status code (should be 400 as admin is already initialized)
            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());

            // Verify the error message
            String errorMessage = Objects.requireNonNull(response.getResolvedException()).getMessage();
            assertTrue(errorMessage.contains("Admin already initialized"));
        }
    }

    @Test
    void testFindUserById() throws Exception {
        String userId = "u7v8rr";
        User user = userRepository.findByUserId(userId);

        MvcResult response = mvc.perform(get("/api/v1/initialize/{id}", userId))
                .andReturn();

        // User with specified id exists
        if (user != null) {
            assertEquals(HttpStatus.CREATED.value(), response.getResponse().getStatus());
            String json = response.getResponse().getContentAsString();


            // Convert JSON response to UserDTO
            ObjectMapper objectMapper = new ObjectMapper();
            UserDTO result = objectMapper.readValue(json, UserDTO.class);

            assertNotNull(result);
            assertEquals(user.getUserId(), result.getUserId());
            assertEquals(user.getFirstName(), result.getFirstName());
            assertEquals(user.getLastName(), result.getLastName());
            assertEquals(user.getEmail(), result.getEmail());
            assertEquals(user.getPassword(), result.getPassword());
        } else {
            // User not found, assert 404 status
            assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponse().getStatus());
        }
    }


    @Test
    void testCheckAdminInitialized() throws Exception {
        // Mock the behavior of adminInitializationService.isAdminInitialized()
        AdminInitializationService adminInitializationService = Mockito.mock(AdminInitializationService.class);
        Mockito.when(adminInitializationService.isAdminInitialized()).thenReturn(true); // Adjust as needed

        // Perform the GET request
        mvc.perform(get("/api/v1/initialize"))
                .andExpect(status().isOk());

        // Simulate the behavior when isAdminInitialized() returns false
        Mockito.when(adminInitializationService.isAdminInitialized()).thenReturn(false);

        // Perform the GET request again
        mvc.perform(get("/api/v1/initialize"))
                .andExpect(status().isOk());
    }

}
