package com.backend.cms.controller;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private PostService postService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @RequestMapping(value = "/{collectionId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createPost(
            @PathVariable String collectionId,
            @Valid @RequestBody CreatePostRequest request) {
        try {
            postService.createPost(collectionId, request);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            LOGGER.error("Collection not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.error("Error creating post: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error creating post");
        }
    }
}
