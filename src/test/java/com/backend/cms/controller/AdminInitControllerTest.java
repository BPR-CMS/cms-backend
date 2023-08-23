package com.backend.cms.controller;

import com.backend.cms.dto.RegisterUserDTO;
import com.backend.cms.request.CreateInitAdminRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminInitControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void testCreateAdmin() throws Exception {
        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Set properties for the request object
        request.setFirstName("admin");
        request.setLastName("Loredana");
        request.setEmail("admin@gmail.com");
        request.setPassword("adminPassword1@q");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request and get the result
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/admin/initialize")
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        if (actualStatusCode == HttpStatus.CREATED.value()) {

            // Assert the status code
            assertEquals(HttpStatus.CREATED.value(), response.getResponse().getStatus());

            // Assert other parts of the response
            String json = response.getResponse().getContentAsString();
            RegisterUserDTO result = objectMapper.readValue(json, RegisterUserDTO.class);

            assertNotNull(result.getUserId());
            assertEquals("admin", result.getFirstName());
            assertEquals("Loredana", result.getLastName());
            assertEquals("admin@gmail.com", result.getEmail());

            // Assert the hashed password
            assertTrue(passwordEncoder.matches("adminPassword1@q", result.getPassword()));
        } else {
            // Assert the status code (should be 400 as admin is already initialized)
            assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus());

            // Verify the error message
            String errorMessage = Objects.requireNonNull(response.getResolvedException()).getMessage();
            assertTrue(errorMessage.contains("Admin already initialized"));
        }
    }

    @Test
    void testCheckAdminInitialized() throws Exception {
        // First GET request when isAdminInitialized() returns true
        mvc.perform(get("/api/v1/admin"))
                .andExpect(status().isOk());

        // Second GET request when isAdminInitialized() returns false
        mvc.perform(get("/api/v1/admin"))
                .andExpect(status().isOk());
    }
}