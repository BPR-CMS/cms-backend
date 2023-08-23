package com.backend.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilsDatabaseService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void resetTestDatabase() {

        // Drop collections for cleanup
        List<String> collectionNames = mongoTemplate.getCollectionNames().stream().toList();
        for (String collectionName : collectionNames) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}
