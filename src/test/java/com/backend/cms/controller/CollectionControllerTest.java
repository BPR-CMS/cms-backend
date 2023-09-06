package com.backend.cms.controller;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.Collection;
import com.backend.cms.model.User;
import com.backend.cms.repository.CollectionRepository;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreateCollectionRequest;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.backend.cms.service.CollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class CollectionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private CollectionRepository collectionRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CollectionService collectionService;

    @InjectMocks
    private CollectionController collectionController;

    private String token;
    private String requestJson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Generate a valid token
        String userId = "ubcy8c";
        token = jwtTokenUtil.generateToken(userId);

        // Defining the behavior of userRepository.findByUserId() to return a mockUser
        User mockUser = new User();
        when(userRepository.findByUserId(eq(userId))).thenReturn(mockUser);

        // Set properties for the request object
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Webinars");
        request.setDescription("description");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            requestJson = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreateCollectionUnauthorized() throws Exception {
        // Test case: Without Authorization token (401 Unauthorized)
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testCreateCollectionEmptyBody() throws Exception {
        // Test case: With Authorization token and empty body (500 Internal Server Error)
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        assertEquals(expectedStatusCode, actualStatusCode);
    }


    @Test
    void testCreateCollectionValid() throws Exception {
        // Mock the behavior of collectionService.findNewId()
        when(collectionService.findNewId()).thenReturn("mockCollectionId");

        // Create a mock collection to return
        Collection mockCollection = new Collection();
        mockCollection.setCollectionId("mockCollectionId");

        // Mock the behavior of collectionService.validateAndSaveCollection()
        doNothing().when(collectionService).saveCollection(any(), any());

        // Test case: With Authorization token and valid body (201 Created)
        MvcResult validResponse = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();
        // Assert the status code
        assertEquals(HttpStatus.CREATED.value(), validResponse.getResponse().getStatus());

        // Assert other parts of the response for valid response
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = validResponse.getResponse().getContentAsString();
        CollectionDTO validResult = objectMapper.readValue(validJson, CollectionDTO.class);
        assertNotNull(validResult.getName());
        assertNotNull(validResult.getDescription());
    }


    @Test
    void testFindCollectionById() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        // Perform the GET request and get the result
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/collections/{id}", mockCollectionId)
                                .header("Authorization", "Bearer " + token))
                .andReturn();

        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();
        // Assert the status code
        assertEquals(HttpStatus.OK.value(), actualStatusCode);

    }

    @Test
    void testFindCollectionByIdNotFound() throws Exception {
        String mockCollectionId = "nonExistentId";  // Using an ID that does not exist
        // Mock the behavior of collectionService.findCollectionFailIfNotFound() to throw an exception
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId)))
                .thenThrow(new NotFoundException());

        // Perform the GET request and get the result
        MvcResult response = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/collections/{id}", mockCollectionId)
                                .header("Authorization", "Bearer " + token))
                .andReturn();
        // Retrieve the actual status code from the response
        int actualStatusCode = response.getResponse().getStatus();

        // Assert the status code
        assertEquals(HttpStatus.NOT_FOUND.value(), actualStatusCode);
    }

    @Test
    void testCreateCollectionInvalidShortName() throws Exception {
        CreateCollectionRequest request = new CreateCollectionRequest();
        // Invalid name
        request.setName("T");
        request.setDescription("description");

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidNameRequestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(invalidNameRequestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testCreateCollectionInvalidLongName() throws Exception {
        CreateCollectionRequest request = new CreateCollectionRequest();
        // Invalid name
        request.setName("TestTestTestTestTestTestTestTest");
        request.setDescription("description");

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidNameRequestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(invalidNameRequestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testCreateCollectionInvalidShortDescription() throws Exception {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Test");
        request.setDescription("d");

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidNameRequestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(invalidNameRequestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        assertEquals(expectedStatusCode, actualStatusCode);
    }


    @Test
    void testCreateCollectionWithNullName() throws Exception {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName(null);
        request.setDescription("d");

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidNameRequestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(invalidNameRequestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testCreateCollectionWithNullDescription() throws Exception {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Test");
        request.setDescription(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidNameRequestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(invalidNameRequestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        assertEquals(expectedStatusCode, actualStatusCode);
    }
}