package com.backend.cms.service;

import com.backend.cms.model.Attribute;
import com.backend.cms.model.Collection;
import com.backend.cms.model.ContentType;
import com.backend.cms.model.Post;
import com.backend.cms.repository.PostRepository;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CollectionService collectionService;

    public void createPost(String collectionId, CreatePostRequest request) {
        Collection collection = collectionService.findCollectionFailIfNotFound(collectionId);

        validateAttributes(collection, request);

        Post newPost = createNewPost(collectionId, request);

        saveNewPost(newPost);
    }

    private void validateAttributes(Collection collection, CreatePostRequest request) {
        for (Attribute attribute : collection.getAttributes()) {
            Object value = request.getAttributes().get(attribute.getName());
            if (attributeIsRequiredAndValueIsNull(attribute, value)) {

                throw new IllegalArgumentException("Required attribute " + attribute.getName() + " is missing.");
            }

            if (isNumberAttributeAndValueValid(attribute, value)) {
                throw new IllegalArgumentException("Attribute must be a number");
            }
        }
    }

    private boolean attributeIsRequiredAndValueIsNull(Attribute attribute, Object value) {
        return attribute.isRequired() && value == null;
    }

    private boolean isNumberAttributeAndValueValid(Attribute attribute, Object value) {
        return attribute.getContentType() == ContentType.NUMBER && value != null && !(value instanceof Number);
    }

//    private boolean isTextAttributeAndValueValid(Attribute attribute, Object value) {
//        return attribute.getContentType() == ContentType.TEXT && value != null && !(value instanceof String);
//    }
//
//    private boolean isRichTextAttributeAndValueValid(Attribute attribute, Object value) {
//        return attribute.getContentType() == ContentType.RICHTEXT && value != null && !(value instanceof String);
//    }
//
//    private boolean isDateAttributeAndValueValid(Attribute attribute, Object value) {
//        return attribute.getContentType() == ContentType.DATE && value != null && !(value instanceof Date);
//    }
//
//    private boolean isMediaAttributeAndValueValid(Attribute attribute, Object value) {
//        return attribute.getContentType() == ContentType.MEDIA && value != null;
//    }

    private Post createNewPost(String collectionId, CreatePostRequest request) {
        Post newPost = new Post();
        newPost.setPostId(Generator.generateId("p"));
        newPost.setCollectionId(collectionId);
        newPost.setAttributes(request.getAttributes());

        return newPost;
    }

    private void saveNewPost(Post newPost) {
        postRepository.save(newPost);
    }
}
