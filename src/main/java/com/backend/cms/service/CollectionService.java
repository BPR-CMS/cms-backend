package com.backend.cms.service;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.Collection;
import com.backend.cms.repository.CollectionRepository;
import com.backend.cms.request.CreateCollectionRequest;
import com.backend.cms.utils.FieldCleaner;
import com.backend.cms.utils.Generator;
import com.backend.cms.utils.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    public Collection findCollectionFailIfNotFound(String id) {
        Collection collection = collectionRepository.findByCollectionId(id);
        if (collection == null) throw new NotFoundException();

        return collection;
    }

    public String findNewId() {
        String id;
        do {
            id = Generator.generateId("c");
        } while (collectionRepository.findByCollectionId(id) != null);
        return id;
    }

    public void save(Collection collection) {
        if (collection != null) {
            collectionRepository.save(collection);
        }
    }

    public List<Collection> findAllCollections() {
        return collectionRepository.findAll();
    }

    public List<Collection> findCollectionByUserId(String userId) {
        return collectionRepository.findAllByUserId(userId);
    }

    public List<CollectionDTO> collectionToDTOs(List<Collection> collection) {
        return collection.stream().map(CollectionDTO::fromCollection).collect(Collectors.toList());
    }

    public void validateAndSaveCollection(Collection collection, CreateCollectionRequest request) {

        try {
            validateUserInput(request);

            Collection cleanedCollection = cleanCollectionFields(collection);
            String cleanedName = cleanCollectionName(request.getName());

            checkForDuplicateName(cleanedName);

            cleanedCollection.setName(cleanedName);
            save(cleanedCollection);
        } catch (NullPointerException ex) {
            // Handle the case where the name is null
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fields cannot be null.");
        }
    }

    private Collection cleanCollectionFields(Collection collection) {
        return FieldCleaner.cleanCollectionFields(collection);
    }

    private String cleanCollectionName(String name) {
        if (name != null) {
            return name.trim();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Collection name cannot be null.");
    }

    private void checkForDuplicateName(String name) {
        Collection existingCollection = collectionRepository.findByName(name);

        if (existingCollection != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A collection with the same name already exists.");
        }
    }

    private void validateUserInput(CreateCollectionRequest request) {
        InputValidator.validateName(request.getName());
        InputValidator.validateDescription(request.getDescription());
    }
}
