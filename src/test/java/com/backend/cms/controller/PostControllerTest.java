package com.backend.cms.controller;

import com.backend.cms.dto.PostDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.*;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.backend.cms.service.PostService;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.Arrays;
import java.util.List;

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
        UserType userRole = UserType.ADMIN;
        token = jwtTokenUtil.generateToken(userId, userRole);
        // Return a mockUser
        User mockUser = new User();
        mockUser.setUserType(UserType.ADMIN);
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

    @Test
    void testGetAllPostsForCollection_SuccessfulRequest() throws Exception {
        // Mock to return a list of posts
        List<Post> mockPosts = Arrays.asList(new Post(), new Post());
        when(postService.findPostsByCollectionId(anyString())).thenReturn(mockPosts);

        String collectionId = "validCollectionId";

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/posts/collection/{collectionId}", collectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                .andReturn();

        assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getResponse().getStatus()));

        ObjectMapper objectMapper = new ObjectMapper();
        List<Post> actualPosts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(mockPosts.size(), actualPosts.size());
    }

    @Test
    void testGetAllPostsForCollection_ErrorRequest() throws Exception {
        // Mock to throw an exception
        when(postService.findPostsByCollectionId(anyString())).thenThrow(new RuntimeException("Error"));

        String collectionId = "validCollectionId";

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/posts/collection/{collectionId}", collectionId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                .andReturn();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.valueOf(result.getResponse().getStatus()));
    }

    @Test
    void testFindPostById_ValidPost() throws Exception {
        // Mock to return a post
        Post mockPost = new Post();
        when(postService.findPostFailIfNotFound(anyString())).thenReturn(mockPost);

        String postId = "validPostId";

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/posts/{id}", postId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                .andReturn();

        assertEquals(HttpStatus.OK, HttpStatus.valueOf(result.getResponse().getStatus()));


        ObjectMapper objectMapper = new ObjectMapper();
        PostDTO actualPostDTO = objectMapper.readValue(result.getResponse().getContentAsString(), PostDTO.class);
        assertNotNull(actualPostDTO);
    }

    @Test
    void testFindPostById_NotFound() throws Exception {
        // Mock to throw NotFoundException
        when(postService.findPostFailIfNotFound(anyString())).thenThrow(new NotFoundException());

        String postId = "nonExistentPostId";

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/posts/{id}", postId)
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json"))
                .andReturn();

        assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(result.getResponse().getStatus()));
    }
}
