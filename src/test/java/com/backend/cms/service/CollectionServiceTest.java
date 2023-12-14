package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.Collection;
import com.backend.cms.repository.CollectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionService collectionService;


    @Test
    void testFindNewId() {
        when(collectionRepository.findByCollectionId(anyString())).thenReturn(null);

        String result = collectionService.findNewId();
        assertNotNull(result);
        assertTrue(result.startsWith("c"));
    }

    @Test
    void testSave() {
        Collection collection = new Collection();

        collectionService.save(collection);

        verify(collectionRepository, times(1)).save(collection);
    }

    @Test
    void testFindCollectionFailIfNotFound_CollectionFound() {
        Collection collection = new Collection();
        when(collectionRepository.findByCollectionId("collectionId")).thenReturn(collection);

        Collection result = collectionService.findCollectionFailIfNotFound("collectionId");

        assertEquals(collection, result);
    }

    @Test
    void testFindCollectionFailIfNotFound_CollectionNotFound() {
        when(collectionRepository.findByCollectionId("collectionId")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            collectionService.findCollectionFailIfNotFound("collectionId");
        });
    }

}