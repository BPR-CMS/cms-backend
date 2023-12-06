package com.backend.cms.service;

import com.backend.cms.model.*;
import com.backend.cms.repository.PostRepository;
import com.backend.cms.request.CreatePostRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CollectionService collectionService;

    @Mock
    private SecurityHelper securityHelper;

    @Test
    void createPost_SuccessfulCreation() {
        // Mocking data
        String collectionId = "validCollectionId";

        // Adding valid values for several attribute fields in the request
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("Title", "Blogpost");
        attributes.put("Age", 15);
        attributes.put("Date", "2023-02-02");
        attributes.put("Heading", "testValue");
        request.setAttributes(attributes);

        // Mocking a collection with valid attributes
        TextAttribute textAttribute = new TextAttribute();
        textAttribute.setName("Title");
        textAttribute.setContentType(ContentType.TEXT);
        textAttribute.setTextType(TextType.SHORT);
        textAttribute.setRequired(true);
        textAttribute.setMinimumLength(2);
        textAttribute.setMaximumLength(50);

        NumberAttribute numberAttribute = new NumberAttribute();
        numberAttribute.setName("Age");
        numberAttribute.setContentType(ContentType.NUMBER);
        numberAttribute.setFormatType(FormatType.INTEGER);
        numberAttribute.setRequired(true);
        numberAttribute.setMinimumValue(2);
        numberAttribute.setMaximumValue(50);

        DateAttribute dateAttribute = new DateAttribute();
        dateAttribute.setName("Date");
        dateAttribute.setContentType(ContentType.DATE);
        dateAttribute.setDateType(DateType.DATE);
        dateAttribute.setRequired(true);

        RichTextAttribute richTextAttribute = new RichTextAttribute();
        richTextAttribute.setName("Heading");
        richTextAttribute.setContentType(ContentType.RICHTEXT);
        richTextAttribute.setRequired(true);
        richTextAttribute.setMinimumLength(2);
        richTextAttribute.setMaximumLength(50);

        Collection mockCollection = new Collection();
        mockCollection.setAttributes(Arrays.asList(textAttribute, numberAttribute, dateAttribute, richTextAttribute));


        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        when(securityHelper.getCurrentUserId()).thenReturn("userId");

        when(postRepository.save(any(Post.class))).thenReturn(new Post());

        // Perform the test
        assertDoesNotThrow(() -> postService.createPost(collectionId, request));
    }

    @Test
    void createPost_EmptyAttributeValue() {
        // Mocking data
        String collectionId = "validCollectionId";
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        // Set empty value
        attributes.put("Title", "");
        request.setAttributes(attributes);


        Collection mockCollection = new Collection();

        // Mocking a collection with a valid attribute
        TextAttribute textAttribute = new TextAttribute();
        textAttribute.setName("Title");
        textAttribute.setContentType(ContentType.TEXT);
        textAttribute.setTextType(TextType.SHORT);
        textAttribute.setRequired(true);
        textAttribute.setMinimumLength(2);
        textAttribute.setMaximumLength(50);
        mockCollection.setAttributes(Collections.singletonList(textAttribute));
        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        // Perform the test
        NullPointerException exception = assertThrows(NullPointerException.class, () -> postService.createPost(collectionId, request));

        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    void createPost_InvalidData_Text_Attribute() {
        // Mocking data
        String collectionId = "validCollectionId";
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        // Adding invalid data for Text attribute
        attributes.put("Title", 234);
        request.setAttributes(attributes);


        Collection mockCollection = new Collection();

        // Mocking a collection with a valid attribute
        TextAttribute textAttribute = new TextAttribute();
        textAttribute.setName("Title");
        textAttribute.setContentType(ContentType.TEXT);
        textAttribute.setTextType(TextType.SHORT);
        textAttribute.setRequired(true);
        textAttribute.setMinimumLength(2);
        textAttribute.setMaximumLength(50);
        mockCollection.setAttributes(Collections.singletonList(textAttribute));
        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        // Perform the test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createPost(collectionId, request));

        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    void createPost_InvalidData_ExceedsMaxLength_Text_Attribute() {
        // Mocking data
        String collectionId = "validCollectionId";
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        // Adding invalid data for Text attribute
        attributes.put("Title", "Length is too long, invalid data");
        request.setAttributes(attributes);


        Collection mockCollection = new Collection();

        // Mocking a collection with a valid attribute
        TextAttribute textAttribute = new TextAttribute();
        textAttribute.setName("Title");
        textAttribute.setContentType(ContentType.TEXT);
        textAttribute.setTextType(TextType.SHORT);
        textAttribute.setRequired(true);
        textAttribute.setMinimumLength(2);
        textAttribute.setMaximumLength(10);
        mockCollection.setAttributes(Collections.singletonList(textAttribute));
        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        // Perform the test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createPost(collectionId, request));

        // Exception message: Attribute 'Title' must have a maximum length of 10
        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    void createPost_InvalidData_BelowMinLength_Text_Attribute() {
        // Mocking data
        String collectionId = "validCollectionId";
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        // Adding invalid data for Text attribute
        attributes.put("Title", "L");
        request.setAttributes(attributes);


        Collection mockCollection = new Collection();

        // Mocking a collection with a valid attribute
        TextAttribute textAttribute = new TextAttribute();
        textAttribute.setName("Title");
        textAttribute.setContentType(ContentType.TEXT);
        textAttribute.setTextType(TextType.SHORT);
        textAttribute.setRequired(true);
        textAttribute.setMinimumLength(2);
        textAttribute.setMaximumLength(10);
        mockCollection.setAttributes(Collections.singletonList(textAttribute));
        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        // Perform the test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createPost(collectionId, request));

        // Exception message: Attribute 'Title' must have a minimum length of 2
        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    void createPost_InvalidData_ExceedsMaxLength_RichText_Attribute() {
        // Mocking data
        String collectionId = "validCollectionId";
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        // Adding invalid data for RichText attribute
        attributes.put("Heading", "Length is too long, invalid data");
        request.setAttributes(attributes);


        Collection mockCollection = new Collection();

        // Mocking a collection with a valid attribute
        RichTextAttribute richTextAttribute = new RichTextAttribute();
        richTextAttribute.setName("Heading");
        richTextAttribute.setContentType(ContentType.RICHTEXT);
        richTextAttribute.setRequired(true);
        richTextAttribute.setMinimumLength(2);
        richTextAttribute.setMaximumLength(10);

        mockCollection.setAttributes(Collections.singletonList(richTextAttribute));
        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        // Perform the test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createPost(collectionId, request));

        // Exception message: Attribute 'Heading' must have a maximum length of 10
        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    void createPost_InvalidData_BelowMinLength_RichText_Attribute() {
        // Mocking data
        String collectionId = "validCollectionId";
        CreatePostRequest request = new CreatePostRequest();
        Map<String, Object> attributes = new HashMap<>();
        // Adding invalid data for RichText attribute
        attributes.put("Heading", "L");
        request.setAttributes(attributes);


        Collection mockCollection = new Collection();

        // Mocking a collection with a valid attribute
        RichTextAttribute richTextAttribute = new RichTextAttribute();
        richTextAttribute.setName("Heading");
        richTextAttribute.setContentType(ContentType.RICHTEXT);
        richTextAttribute.setRequired(true);
        richTextAttribute.setMinimumLength(2);
        richTextAttribute.setMaximumLength(10);

        mockCollection.setAttributes(Collections.singletonList(richTextAttribute));
        when(collectionService.findCollectionFailIfNotFound(eq(collectionId))).thenReturn(mockCollection);

        // Perform the test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createPost(collectionId, request));

        // Exception message: Attribute 'Heading' must have a minimum length of 2
        System.out.println("Exception message: " + exception.getMessage());
    }

}
