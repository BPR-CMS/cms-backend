package com.backend.cms.repository;

import com.backend.cms.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>, QuerydslPredicateExecutor<User> {

    @Query("{'email':?0}")
    User findByEmail(String email);

    @Query("{'userId':?0}")
    User findByUserId(String userId);
}
