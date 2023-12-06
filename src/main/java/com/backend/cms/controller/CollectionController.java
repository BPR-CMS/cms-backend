package com.backend.cms.controller;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.model.*;
import com.backend.cms.request.CreateAttributeRequest;
import com.backend.cms.request.CreateCollectionRequest;
import com.backend.cms.service.CollectionService;
import com.backend.cms.service.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:3000", "https://cms-backend-production-0a2c.up.railway.app/"})
@RestController
@RequestMapping("/api/v1/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SecurityHelper securityHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionDTO create(@Valid @RequestBody CreateCollectionRequest request) {
        LOGGER.info("Creating a collection entry with information: {}", request);
        Collection collection = request.toCollection();
        collection.setCollectionId(collectionService.findNewId());
        collection.setUserId(securityHelper.getCurrentUserId());
        collectionService.saveCollection(collection, request);
        return CollectionDTO.fromCollection(collection);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public CollectionDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding collection entry with id: {}", id);
        Collection collection = collectionService.findCollectionFailIfNotFound(id);
        return CollectionDTO.fromCollection(collection);
    }

    @RequestMapping(value = "name/{apiId}", method = RequestMethod.GET)
    public CollectionDTO findByApiId(@PathVariable("apiId") String apiId) {
        LOGGER.info("Finding collection entry with name (apiId): {}", apiId);
        Collection collection = collectionService.findCollectionByNameFailIfNotFound(apiId);
        return CollectionDTO.fromCollection(collection);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CollectionDTO> findAllCollections() {
        LOGGER.info("Finding all collection entries");
        List<Collection> collections = collectionService.findAllCollections();
        return collections.stream()
                .map(CollectionDTO::fromCollection)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public List<CollectionDTO> findAll() {
        LOGGER.info("Finding all user collections");
        // Get the currently authenticated user's ID
        List<Collection> collection = collectionService.findCollectionByUserId(securityHelper.getCurrentUserId());
        return collectionService.collectionToDTOs(collection);
    }

    @RequestMapping(value = "/{collectionId}/attributes", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Attribute> addAttribute(
            @PathVariable String collectionId, @Valid
    @RequestBody CreateAttributeRequest request) {
        LOGGER.info("Adding attribute to a collection entry with information: {}", request);
        Attribute attribute = collectionService.createAttributeInstance(request);

        if (attribute != null) {
            collectionService.addAttributeToCollection(collectionId, attribute, request);
            return ResponseEntity.ok(attribute);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
