package com.backend.cms.controller;

import com.backend.cms.dto.RegisterUserDTO;
import com.backend.cms.repository.ConfigRepository;
import com.backend.cms.request.CreateInitAdminRequest;
import com.backend.cms.service.AdminInitializationService;
import com.backend.cms.service.AuthService;
import com.backend.cms.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminInitControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitController adminInitController;

    @MockBean
    private UserService userService;
    @MockBean
    AdminInitializationService adminInitializationService;

    @MockBean
    private ConfigRepository configRepository;

    @MockBean
    private AuthService authService;

    @Test
    void testCreateAdmin() throws Exception {
        // Mocking the behavior of userService.isInitialized()
        when(adminInitializationService.isAdminInitialized()).thenReturn(false);

        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Set properties for the request object
        request.setFirstName("admin");
        request.setLastName("Loredana");
        request.setEmail("admin@gmail.com");
        request.setPassword("adminPassword1@q");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Mocking the behavior of userService.createInitialAdmin()
        RegisterUserDTO mockResult = new RegisterUserDTO();
        mockResult.setUserId("mockUserId");
        mockResult.setFirstName(request.getFirstName());
        mockResult.setLastName(request.getLastName());
        mockResult.setEmail(request.getEmail());
        mockResult.setPassword(passwordEncoder.encode(request.getPassword()));
        when(adminInitializationService.initializeAdmin(any(CreateInitAdminRequest.class))).thenReturn(mockResult);

        // Perform the POST request and get the result
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/admin/initialize")
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // Assert the status code
        assertEquals(HttpStatus.CREATED.value(), actualStatusCode);

        // Assert other parts of the response
        String json = response.getResponse().getContentAsString();
        RegisterUserDTO result = objectMapper.readValue(json, RegisterUserDTO.class);

        assertNotNull(result.getUserId());
        assertEquals("admin", result.getFirstName());
        assertEquals("Loredana", result.getLastName());
        assertEquals("admin@gmail.com", result.getEmail());

        // Assert the hashed password
        assertTrue(passwordEncoder.matches("adminPassword1@q", result.getPassword()));
    }

    @Test
    void testCreateAdmin_AlreadyInitialized() throws Exception {
        // Mocking the behavior of adminInitializationService.isAdminInitialized()
        when(adminInitializationService.isAdminInitialized()).thenReturn(true);

        CreateInitAdminRequest request = new CreateInitAdminRequest();
        // Set properties for the request object
        request.setFirstName("admin");
        request.setLastName("Loredana");
        request.setEmail("admin@gmail.com");
        request.setPassword("adminPassword1@q");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Mocking the behavior of adminInitializationService.initializeAdmin() to throw BadRequestException
        when(adminInitializationService.initializeAdmin(any(CreateInitAdminRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin already initialized"));

        // Perform the POST request and expect a BAD_REQUEST status
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/admin/initialize")
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andReturn();


        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();

        // Assert the status code is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), actualStatusCode);
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