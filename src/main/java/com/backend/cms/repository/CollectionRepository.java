package com.backend.cms.repository;

import com.backend.cms.model.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String>, QuerydslPredicateExecutor<Collection> {

    @Query("{'collectionId':?0}")
    Collection findByCollectionId(String collectionId);

    @Query("{'userId':?0}")
    List<Collection> findAllByUserId(String userId);

    @Query("{'name':?0}")
    Collection findByName(String name);

}
