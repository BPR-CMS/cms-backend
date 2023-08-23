package com.backend.cms.controller;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.model.Collection;
import com.backend.cms.request.CreateCollectionRequest;
import com.backend.cms.service.CollectionService;
import com.backend.cms.service.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SecurityHelper securityHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionDTO create(@RequestBody CreateCollectionRequest request) {
        LOGGER.info("Creating a collection entry with information: {}", request);
        Collection collection = request.toCollection();
        collection.setCollectionId(collectionService.findNewId());
        collection.setUserId(securityHelper.getCurrentUserId());
        collectionService.save(collection);
        return CollectionDTO.fromCollection(collection);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    CollectionDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding collection entry with id: {}", id);
        Collection collection = collectionService.findCollectionFailIfNotFound(id);
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

//    @RequestMapping(method = RequestMethod.GET)
//    List<CollectionDTO> findAll() {
//        LOGGER.info("Finding all user collections");
//        // Get the currently authenticated user's ID
//        List<Collection> collection = collectionService.findCollectionByUserId(securityHelper.getCurrentUserId());
//        return collectionService.collectionToDTOs(collection);
//    }
}
