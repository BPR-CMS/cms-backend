package com.backend.cms.repository;

import com.backend.cms.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String>, QuerydslPredicateExecutor<Post> {
}
