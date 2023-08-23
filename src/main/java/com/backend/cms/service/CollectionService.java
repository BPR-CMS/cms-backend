package com.backend.cms.service;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.Collection;
import com.backend.cms.repository.CollectionRepository;
import com.backend.cms.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
