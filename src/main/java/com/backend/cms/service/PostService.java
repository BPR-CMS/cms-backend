package com.backend.cms.service;

import com.backend.cms.model.*;
import com.backend.cms.model.Collection;
import com.backend.cms.repository.PostRepository;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CollectionService collectionService;

    public void createPost(String collectionId, CreatePostRequest request) {
        Collection collection = collectionService.findCollectionFailIfNotFound(collectionId);

        validatePostAttributes(collection.getAttributes(), request.getAttributes());

        Post newPost = createNewPost(collectionId, request);

        saveNewPost(newPost);
    }

    private void validatePostAttributes(List<Attribute> collectionAttributes, Map<String, Object> postAttributes) {
        for (Attribute collectionAttribute : collectionAttributes) {
            String attributeName = collectionAttribute.getName();

            if (postAttributes.containsKey(attributeName)) {
                Object attributeValue = postAttributes.get(attributeName);
                validateAttributeValue(collectionAttribute, attributeValue);
            } else if (collectionAttribute.isRequired()) {
                throw new IllegalArgumentException("Attribute '" + attributeName + "' is required.");
            }
        }
    }

    private void validateAttributeValue(Attribute collectionAttribute, Object attributeValue) {
        switch (collectionAttribute.getContentType()) {
            case TEXT:
                validateTextAttribute((TextAttribute) collectionAttribute, attributeValue);
                break;
            case NUMBER:
                validateNumberAttribute((NumberAttribute) collectionAttribute, attributeValue);
                break;
            case RICHTEXT:
                validateRichTextAttribute((RichTextAttribute) collectionAttribute, attributeValue);
                break;
        }
    }

    private void validateTextAttribute(TextAttribute collectionAttribute, Object attributeValue) {
        String textValue = attributeValue.toString();

        if (textValue.length() < collectionAttribute.getMinimumLength()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a minimum length of " + collectionAttribute.getMinimumLength());
        }
        if (textValue.length() > collectionAttribute.getMaximumLength()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a maximum length of " + collectionAttribute.getMaximumLength());
        }
        String regexPattern = "^(?=.*[a-zA-Z])[a-zA-Z0-9\\s.,!?]*$";

        if (textValue.length() > 0 && !textValue.matches(regexPattern)) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must match the pattern: " + regexPattern);
        }

        if (collectionAttribute.isUnique() && isAttributeValueNotUnique(collectionAttribute.getName(), textValue)) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a unique value.");
        }
    }

    private void validateRichTextAttribute(RichTextAttribute collectionAttribute, Object attributeValue) {
        String richTextValue = attributeValue.toString();

        if (richTextValue.length() < collectionAttribute.getMinimumLength()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a minimum length of " + collectionAttribute.getMinimumLength());
        }

        if (richTextValue.length() > collectionAttribute.getMaximumLength()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a maximum length of " + collectionAttribute.getMaximumLength());
        }
        String regexPattern = "^(?=.*[a-zA-Z])[a-zA-Z0-9\\s.,!?]*$";

        if (richTextValue.length() > 0 && !richTextValue.matches(regexPattern)) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must match the pattern: " + regexPattern);
        }

    }
        private void validateNumberAttribute(NumberAttribute collectionAttribute, Object attributeValue) {
        if ( ((Number) attributeValue).intValue() < collectionAttribute.getMinimumValue()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a minimum value of " + collectionAttribute.getMinimumValue());
        }
        if ( ((Number) attributeValue).intValue() > collectionAttribute.getMaximumValue()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a maximum value of " + collectionAttribute.getMaximumValue());
        }
    }


    private boolean isAttributeValueNotUnique(String attributeName, String attributeValue) {
        // Retrieve existing posts from the database
        List<Post> allPosts = postRepository.findByAttributeNameAndValue(attributeName, ".*" + attributeValue.trim() + ".*");

        // Check if the attribute value is present in the existing posts
        for (Post post : allPosts) {
            for (Map.Entry<String, Object> entry : post.getAttributes().entrySet()) {
                String postAttributeName = entry.getKey();
                Object postAttributeValue = entry.getValue();

                // Compare ignoring case and white spaces
                if (attributeName.equals(postAttributeName) && areAttributeValuesEqual(attributeValue, postAttributeValue)) {
                    // Value is already present in the existing posts for this attribute
                    return true;
                }
            }
        }

        return false;
    }

    private boolean areAttributeValuesEqual(String value1, Object value2) {
        if (value2 instanceof String) {
            // Compare ignoring case and white spaces after trimming
            return value1.trim().equalsIgnoreCase(((String) value2).trim());
        }

        return false;
    }


private Post createNewPost(String collectionId, CreatePostRequest request) {
    Post newPost = new Post();
    newPost.setPostId(Generator.generateId("p"));
    newPost.setCollectionId(collectionId);

    Map<String, Object> trimmedAttributes = new HashMap<>();
    for (Map.Entry<String, Object> entry : request.getAttributes().entrySet()) {
        String attributeName = entry.getKey();
        Object attributeValue = entry.getValue();

        if (attributeValue instanceof String) {
            trimmedAttributes.put(attributeName, ((String) attributeValue).trim());
        } else {
            trimmedAttributes.put(attributeName, attributeValue);
        }
    }

    newPost.setAttributes(trimmedAttributes);

    return newPost;
}

    private void saveNewPost(Post newPost) {
        postRepository.save(newPost);
    }
}
