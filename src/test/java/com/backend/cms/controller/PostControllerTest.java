package com.backend.cms.controller;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.*;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.backend.cms.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private PostService postService;
    @MockBean
    private UserRepository userRepository;
    private String token;
    private String requestJson;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Generate a valid token
        String userId = "ubcy8c";
        token = jwtTokenUtil.generateToken(userId);
      // Return a mockUser
        User mockUser = new User();
        when(userRepository.findByUserId(eq(userId))).thenReturn(mockUser);


        CreatePostRequest request = new CreatePostRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            requestJson = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testCreatePostCollectionNotFound() throws Exception {
        // Mocking the behavior of postService.createPost() to throw NotFoundException
      doThrow(new NotFoundException()).when(postService).createPost(anyString(), any());

        String mockCollectionId = "nonExistentId";

        // Test case: Collection not found (404 Not Found)
        MvcResult notFoundResponse = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/posts/{collectionId}", mockCollectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();

        // Assert the status code
        assertEquals(HttpStatus.NOT_FOUND.value(), notFoundResponse.getResponse().getStatus());
    }

    @Test
    void testCreatePostUnauthorized() throws Exception {
        // Create a mock collection to return
        Collection mockCollection = new Collection();
        mockCollection.setCollectionId("validCollectionId");
        // Test case: Without Authorization token (401 Unauthorized)
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/posts/{collectionId}", mockCollection)
                                .contentType("application/json")
                                .content(requestJson))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    void testCreatePostEmptyBody() throws Exception {
        // Create a mock collection to return
        Collection mockCollection = new Collection();
        mockCollection.setCollectionId("validCollectionId");

        // Test case: With Authorization token and empty body (400 Bad Request)
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/posts/{collectionId}", mockCollection)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                .andReturn();
        int actualStatusCode = result.getResponse().getStatus();
        int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

        assertEquals(expectedStatusCode, actualStatusCode);
    }
}
