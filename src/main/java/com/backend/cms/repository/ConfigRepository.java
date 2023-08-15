package com.backend.cms.repository;

import com.backend.cms.model.Config;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ConfigRepository extends MongoRepository<Config, String>, QuerydslPredicateExecutor<Config> {

    Config findFirst();
}
