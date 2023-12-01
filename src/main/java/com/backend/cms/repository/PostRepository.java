package com.backend.cms.repository;

import com.backend.cms.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String>, QuerydslPredicateExecutor<Post> {

    @Query("{'attributes.?0': {$regex: ?1, $options: 'i'}}")
    List<Post> findByAttributeNameAndValue(@Param("attributeName") String attributeName, @Param("attributeValue") String attributeValue);

    List<Post> findByCollectionId(String collectionId);

    @Query("{'postId':?0}")
    Post findByPostId(String postId);
}
