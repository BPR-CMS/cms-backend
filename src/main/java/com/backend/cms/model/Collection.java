package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(collection = "collection-type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {

    @Id
    private ObjectId _id;

    @Indexed(unique = true)
    private String collectionId;
    @Indexed(unique = true)
    private String name;
    private String description;
    private String userId;

    public Collection(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
