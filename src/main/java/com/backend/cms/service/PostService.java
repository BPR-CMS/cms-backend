package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.*;
import com.backend.cms.model.Collection;
import com.backend.cms.repository.PostRepository;
import com.backend.cms.request.CreatePostRequest;
import com.backend.cms.request.EditPostRequest;
import com.backend.cms.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SecurityHelper securityHelper;

    public void createPost(String collectionId, CreatePostRequest request) {
        Collection collection = collectionService.findCollectionFailIfNotFound(collectionId);

        // Set default values for empty or null attributes
        setDefaultValuesForAttributes(collection.getAttributes(), request.getAttributes());

        validatePostAttributes(collectionId, collection.getAttributes(), request.getAttributes());

        Post newPost = createNewPost(collectionId, request);

        saveNewPost(newPost);
    }

    public List<Post> findPostsByCollectionId(String collectionId) {

        return postRepository.findByCollectionId(collectionId);
    }

    public Post findPostFailIfNotFound(String id) {
        Post post = postRepository.findByPostId(id);
        if (post == null) throw new NotFoundException();

        return post;
    }

    private void setDefaultValuesForAttributes(List<Attribute> collectionAttributes, Map<String, Object> postAttributes) {
        for (Attribute collectionAttribute : collectionAttributes) {
            String attributeName = collectionAttribute.getName();

            if (!postAttributes.containsKey(attributeName) || postAttributes.get(attributeName) == null) {
                setDefaultValueForAttributeType(collectionAttribute, postAttributes);
            } else if (postAttributes.get(attributeName).toString().isEmpty()) {
                setDefaultValueForAttributeType(collectionAttribute, postAttributes);
            }
        }
    }


    private void setDefaultValueForAttributeType(Attribute collectionAttribute, Map<String, Object> postAttributes) {
        if (collectionAttribute instanceof DateAttribute) {
            DateAttribute dateAttribute = (DateAttribute) collectionAttribute;
            postAttributes.put(collectionAttribute.getName(), dateAttribute.getDefaultValue());
        } else if (collectionAttribute instanceof TextAttribute) {
            TextAttribute textAttribute = (TextAttribute) collectionAttribute;
            postAttributes.put(collectionAttribute.getName(), textAttribute.getDefaultValue());
        } else if (collectionAttribute instanceof NumberAttribute) {
            NumberAttribute numberAttribute = (NumberAttribute) collectionAttribute;
            postAttributes.put(collectionAttribute.getName(), numberAttribute.getDefaultValue());
        } else if (collectionAttribute instanceof RichTextAttribute) {
            RichTextAttribute richTextAttribute = (RichTextAttribute) collectionAttribute;
            postAttributes.put(collectionAttribute.getName(), richTextAttribute.getDefaultValue());
        }
    }


    private void validatePostAttributes(String collectionId, List<Attribute> collectionAttributes, Map<String, Object> postAttributes) {
        if (postAttributes == null) {
            throw new IllegalArgumentException("postAttributes cannot be null.");
        }

        boolean foundRequiredAttribute = false;
        boolean foundNonRequiredAttributeWithNonEmptyValue = false;

        for (Attribute collectionAttribute : collectionAttributes) {
            String attributeName = collectionAttribute.getName();
            if (postAttributes.containsKey(attributeName)) {
                Object attributeValue = postAttributes.get(attributeName);
                validateAttributeValue(collectionId, collectionAttribute, attributeValue);

                if (collectionAttribute.isRequired()) {
                    foundRequiredAttribute = true;
                } else if (attributeValue != null && !attributeValue.toString().isEmpty()) {
                    foundNonRequiredAttributeWithNonEmptyValue = true;
                }
            }
        }

        if (!foundRequiredAttribute && !foundNonRequiredAttributeWithNonEmptyValue) {
            throw new IllegalArgumentException("A post cannot be created with empty data");
        }
    }


    private void validateAttributeValue(String collectionId, Attribute collectionAttribute, Object attributeValue) {
        Object valueToValidate;

        if (collectionAttribute.isRequired() && attributeValue.toString().isEmpty()) {
            throw new IllegalArgumentException("Required attribute " + collectionAttribute.getName() + " cannot be null");
        } else {

            valueToValidate = (attributeValue != null) ? attributeValue : getDefaultAttributeValue(collectionAttribute);
        }

        // Skip validation for non-required attributes with default values
        if (!collectionAttribute.isRequired() && valueToValidate.equals(getDefaultAttributeValue(collectionAttribute))) {
            return;
        }

        switch (collectionAttribute.getContentType()) {
            case TEXT:
                validateTextAttribute(collectionId, (TextAttribute) collectionAttribute, valueToValidate);
                break;
            case NUMBER:
                validateNumberAttribute((NumberAttribute) collectionAttribute, valueToValidate);
                break;
            case RICHTEXT:
                validateRichTextAttribute((RichTextAttribute) collectionAttribute, valueToValidate);
                break;
            case DATE:
                validateDateAttribute((DateAttribute) collectionAttribute, valueToValidate);
                break;
        }
    }


    private Object getDefaultAttributeValue(Attribute collectionAttribute) {

        switch (collectionAttribute.getContentType()) {
            case TEXT:
            case DATE:
            case RICHTEXT:
                return "";
            case NUMBER:
                return "";
            default:
                return null;
        }
    }

    private void validateTextAttribute(String collectionId, TextAttribute collectionAttribute, Object attributeValue) {

        String textValue = attributeValue.toString();

        if (textValue.length() < collectionAttribute.getMinimumLength()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a minimum length of " + collectionAttribute.getMinimumLength());
        }

        Integer maximumLength = collectionAttribute.getMaximumLength();
        if (maximumLength != null && textValue.length() > maximumLength) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a maximum length of " + maximumLength);
        }
        String regexPattern = "^(?=.*[a-zA-Z])[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$";


        if (textValue.length() > 0 && !textValue.matches(regexPattern)) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must match a certain pattern ");
        }

        if (collectionAttribute.isUnique() && isAttributeValueNotUnique(collectionId, collectionAttribute.getName(), textValue)) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a unique value.");
        }
    }

    private void validateRichTextAttribute(RichTextAttribute collectionAttribute, Object attributeValue) {

        String richTextValue = attributeValue.toString();

        if (richTextValue.length() < collectionAttribute.getMinimumLength()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a minimum length of " + collectionAttribute.getMinimumLength());
        }

        Integer maximumLength = collectionAttribute.getMaximumLength();
        if (maximumLength != null && richTextValue.length() > maximumLength) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a maximum length of " + maximumLength);
        }

    }

    private void validateNumberAttribute(NumberAttribute collectionAttribute, Object attributeValue) {

        int attributeIntValue = ((Number) attributeValue).intValue();

        if (attributeIntValue < collectionAttribute.getMinimumValue()) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a minimum value of " + collectionAttribute.getMinimumValue());
        }

        Integer maximumValue = collectionAttribute.getMaximumValue();
        if (maximumValue != null && attributeIntValue > maximumValue) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must have a maximum value of " + maximumValue);
        }
    }

    private void validateDateAttribute(DateAttribute collectionAttribute, Object attributeValue) {

        String dateValue = attributeValue.toString();

        if (attributeValue.toString().isEmpty()) {
            dateValue = collectionAttribute.getDefaultValue();
        }

        try {
            SimpleDateFormat dateFormat;

            switch (collectionAttribute.getDateType()) {
                case DATE:
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                case DATETIME:
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    break;
                case TIME:
                    dateFormat = new SimpleDateFormat("HH:mm");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid DateType specified for attribute '" + collectionAttribute.getName() + "'");
            }

            dateFormat.setLenient(false);
            Date parsedDate = dateFormat.parse(dateValue);

            dateValue = dateFormat.format(parsedDate);

        } catch (ParseException e) {
            throw new IllegalArgumentException("Attribute '" + collectionAttribute.getName() + "' must be a valid format.");
        }
    }

    private boolean isAttributeValueNotUnique(String collectionId, String attributeName, String attributeValue) {

        List<Post> allPosts = postRepository.findByCollectionAndAttributeNameAndValue(collectionId, attributeName, ".*" + attributeValue.trim() + ".*");
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
        newPost.setUserId(securityHelper.getCurrentUserId());
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

    public void updatePost(String collectionId, String postId, EditPostRequest request) {
        Post existingPost = findPostFailIfNotFound(postId);
        Collection collection = collectionService.findCollectionFailIfNotFound(collectionId);
        Map<String, Object> updatedAttributes = request.getAttributes();
        if (updatedAttributes != null && !updatedAttributes.isEmpty()) {

            validatePostAttributes(collectionId, collection.getAttributes(), request.getAttributes());
            existingPost.getAttributes().putAll(updatedAttributes);
        }

        saveNewPost(existingPost);
    }

}
