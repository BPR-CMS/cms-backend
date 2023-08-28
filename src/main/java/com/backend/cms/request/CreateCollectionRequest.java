package com.backend.cms.request;

import com.backend.cms.model.Collection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCollectionRequest {

    private String name;
    private String description;

    public Collection toCollection() {
        return new Collection(name, description);
    }
}
