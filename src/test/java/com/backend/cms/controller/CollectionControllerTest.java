package com.backend.cms.controller;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.*;
import com.backend.cms.repository.CollectionRepository;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreateAttributeRequest;
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

    @Test
    void testAddAttribute_ValidData() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);
        Attribute mockAttribute = new Attribute();

        CreateAttributeRequest request = new CreateAttributeRequest();

        request.setName("Attribute");

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.OK.value();

        // Assert that the response status code is HttpStatus.CREATED (201)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddTextAttribute_ValidData() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setName("Test attribute");
        request.setContentType(ContentType.TEXT);
        request.setTextType(TextType.SHORT);
        request.setRequired(true);
        // @Min(value = 2, message = "Length must be at least 2.")
        request.setMinimumLength(2);
        //  @Max(value = 50, message = "Length cannot exceed 50.")
        request.setMaximumLength(50);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.OK.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddRichTextAttribute_ValidData() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setName("Test attribute");
        request.setContentType(ContentType.RICHTEXT);
        request.setRequired(true);
        // @Min(value = 2, message = "Length must be at least 2.")
        request.setMinimumLength(2);
        //   @Max(value = 5000, message = "Length cannot exceed 5000.")
        request.setMaximumRichTextLength(5000);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.OK.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddMediaAttribute_ValidData() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setName("Test attribute");
        request.setContentType(ContentType.MEDIA);
        request.setRequired(false);
        request.setMediaType(MediaType.MULTIPLE);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.OK.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddDateAttribute_ValidData() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setName("Test attribute");
        request.setContentType(ContentType.DATE);
        request.setRequired(false);
        request.setDateType(DateType.DATE);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.OK.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddAttribute_InvalidEmptyRequest() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);
        // Mock the behavior of collectionService.createAttributeInstance to return null (indicating invalid data)
        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(null);

        CreateAttributeRequest request = new CreateAttributeRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddAttribute_InvalidLongName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        // Invalid data
        request.setName("long Name long Name long Name long Name long Name long Name");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddAttribute_InvalidShortName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        // Invalid data
        request.setName("I");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddAttribute_InvalidNumericName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        // Invalid data
        request.setName("1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddTextAttribute_EmptyName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setContentType(ContentType.TEXT);
        request.setTextType(TextType.SHORT);
        request.setRequired(true);
        // @Min(value = 2, message = "Length must be at least 2.")
        request.setMinimumLength(2);
        //  @Max(value = 50, message = "Length cannot exceed 50.")
        request.setMaximumLength(50);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddRichTextAttribute_EmptyName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setContentType(ContentType.RICHTEXT);
        request.setRequired(true);
        // @Min(value = 2, message = "Length must be at least 2.")
        request.setMinimumLength(2);
        //   @Max(value = 5000, message = "Length cannot exceed 5000.")
        request.setMaximumRichTextLength(5000);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddMediaAttribute_EmptyName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setContentType(ContentType.MEDIA);
        request.setRequired(false);
        request.setMediaType(MediaType.MULTIPLE);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddDateAttribute_EmptyName() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();
        request.setContentType(ContentType.DATE);
        request.setRequired(false);
        request.setDateType(DateType.DATE);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddTextAttribute_InvalidLength() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();

        request.setName("Test attribute");
        request.setContentType(ContentType.TEXT);
        // Invalid data
        // @Min(value = 2, message = "Length must be at least 2.")
        request.setMinimumLength(1);
        //  @Max(value = 50, message = "Length cannot exceed 50.")
        request.setMaximumLength(51);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddNumberAttribute_InvalidValues() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();

        // Mock the behavior of collectionService.findCollectionFailIfNotFound()
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        Attribute mockAttribute = new Attribute();

        when(collectionService.createAttributeInstance(any(CreateAttributeRequest.class))).thenReturn(mockAttribute);

        // Set properties for the request object with invalid data
        CreateAttributeRequest request = new CreateAttributeRequest();

        request.setName("Test attribute");
        request.setContentType(ContentType.NUMBER);
        // Invalid data
        // @Min(value = 0, message = "Value must be at least 0.")
        request.setMinimumValue(-1);

        //  @Max(value = 40, message = "Value cannot exceed 40.")
        request.setMaximumValue(41);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request with a valid collectionId and request body
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value(); // Expecting 400 Bad Request

        // Assert that the response status code is HttpStatus.BAD_REQUEST (400)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testAddAttribute_DuplicateNameConflict() throws Exception {
        String mockCollectionId = "mockCollectionId";
        Collection mockCollection = new Collection();
        when(collectionService.findCollectionFailIfNotFound(eq(mockCollectionId))).thenReturn(mockCollection);

        // Create two attributes with the same name
        String attributeName = "DuplicateAttribute";
        Attribute mockAttribute = new Attribute();
        mockAttribute.setName(attributeName);
        CreateAttributeRequest request1 = new CreateAttributeRequest();
        request1.setName(attributeName);

        CreateAttributeRequest request2 = new CreateAttributeRequest();
        request2.setName(attributeName);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson1 = objectMapper.writeValueAsString(request1);
        String requestJson2 = objectMapper.writeValueAsString(request2);

        // Simulate adding the first attribute
        when(collectionService.createAttributeInstance(eq(request1))).thenReturn(mockAttribute);

        // Perform the first POST request
        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson1))
                .andReturn();

        // Simulate adding the second attribute with the same name
        when(collectionService.createAttributeInstance(eq(request2))).thenReturn(null); // Return null to indicate conflict

        // Perform the second POST request
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/collections/{collectionId}/attributes", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson2))
                .andReturn();

        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        // Assert that the response status code is HttpStatus.CONFLICT (409)
        assertEquals(expectedStatusCode, actualStatusCode);
    }

}