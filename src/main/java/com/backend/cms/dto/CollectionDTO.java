package com.backend.cms.dto;

import com.backend.cms.model.Attribute;
import com.backend.cms.model.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CollectionDTO {

    private String id;
    private String name;
    private String description;
    private String userId;

    private List<Attribute> attributes = new ArrayList<>();

    public CollectionDTO(Collection collection) {
        this.id = collection.getCollectionId();
        this.name = collection.getName();
        this.description = collection.getDescription();
        this.userId = collection.getUserId();
        this.attributes = collection.getAttributes();
    }

    public static CollectionDTO fromCollection(Collection collection) {
        return new CollectionDTO(collection);
    }
}
