package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    @Indexed(unique = true)
    private String attributeId;

    private String name;
    private ContentType contentType;
    private boolean required;
}
