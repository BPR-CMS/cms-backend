package com.backend.cms.controller;

import com.backend.cms.dto.PostDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.Post;
import com.backend.cms.repository.PostRepository;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.request.EditPostRequest;
import com.backend.cms.service.AuthService;
import com.backend.cms.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://webease-frontend.vercel.app"})
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PostRepository postRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @RequestMapping(value = "/{collectionId}", method = RequestMethod.POST)
    public ResponseEntity<?> createPost(
            @PathVariable String collectionId,
            @Valid @RequestBody CreatePostRequest request) {
        try {
            authService.checkIfUserIsEditorOrAdminOrThrowException();
            postService.createPost(collectionId, request);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            LOGGER.error("Collection not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.error("Error creating post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/collection/{collectionId}", method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getAllPostsForCollection(@PathVariable String collectionId) {
        try {
            List<Post> posts = postService.findPostsByCollectionId(collectionId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            LOGGER.error("Error getting posts for collection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public PostDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding post entry with id: {}", id);
        Post post = postService.findPostFailIfNotFound(id);
        return PostDTO.fromPost(post);
    }

    @RequestMapping(value = "/{collectionId}/{postId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updatePost(@PathVariable String collectionId,
                                        @PathVariable String postId,
                                        @Valid @RequestBody EditPostRequest request) {
        try {
            authService.checkIfUserIsEditorOrAdminOrThrowException();
            postService.updatePost(collectionId, postId, request);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            LOGGER.error("Post or Collection not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.error("Error updating post: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public PostDTO delete(@PathVariable("id") String id) {
        LOGGER.info("Deleting post entry with id: {}", id);
        authService.checkIfUserIsEditorOrAdminOrThrowException();
        Post post = postService.findPostFailIfNotFound(id);
        postRepository.delete(post);
        LOGGER.info("Deleted post entry with information: {}", post);
        return PostDTO.fromPost(post);
    }
}

