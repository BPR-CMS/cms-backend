package com.backend.cms.dto;

import com.backend.cms.model.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CollectionDTO {

    private String id;
    private String name;
    private String description;
    private String userId;

    public CollectionDTO(Collection collection) {
        this.id = collection.getCollectionId();
        this.name = collection.getName();
        this.description = collection.getDescription();
        this.userId = collection.getUserId();
    }

    public static CollectionDTO fromCollection(Collection collection) {
        return new CollectionDTO(collection);
    }
}
