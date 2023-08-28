package com.backend.cms.service;

import com.backend.cms.model.Collection;
import com.backend.cms.repository.CollectionRepository;
import com.backend.cms.request.CreateCollectionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionService collectionService;

    @Test
    void testValidateAndSaveCollectionWithNullName() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("Fields cannot be null.", exception.getReason());
    }

    @Test
    void testValidateAndSaveCollectionWithEmptyName() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid name format. Name must be 2-20 characters long.", exception.getReason());
    }

    @Test
    void testValidateAndSaveCollectionWithShortName() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("W");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid name format. Name must be 2-20 characters long.", exception.getReason());
    }

    @Test
    void testValidateAndSaveCollectionWithLongName() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Webinar Webinar Webinar Webinar Webinar");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid name format. Name must be 2-20 characters long.", exception.getReason());
    }

    @Test
    void testValidateAndSaveCollectionWithInvalidName() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Webinar1@");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateAndSaveCollectionWithNullDescription() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setDescription(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("Fields cannot be null.", exception.getReason());
    }

    @Test
    void testValidateAndSaveCollectionWithEmptyDescription() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Webinar");
        request.setDescription("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateAndSaveCollectionWithShortNameDescription() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Webinar");
        request.setDescription("D");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateAndSaveCollectionWithLongDescription() {
        CreateCollectionRequest request = new CreateCollectionRequest();
        request.setName("Webinar");
        request.setDescription("Webinar Webinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar Webinar Webinar Webinar Webinar Webinar Webinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar WebinarWebinar Webinar Webinar Webinar Webinar");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            collectionService.validateAndSaveCollection(new Collection(), request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}