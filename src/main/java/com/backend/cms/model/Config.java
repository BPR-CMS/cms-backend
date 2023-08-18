package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {

    private String id;
    private boolean isInitialized;

    public Config(boolean initialized) {
        this.isInitialized = initialized;
    }
}
