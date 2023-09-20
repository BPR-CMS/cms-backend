package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaAttribute extends Attribute {
    private MediaType mediaType;

    public MediaAttribute(String attributeId, String name, ContentType contentType, boolean required, MediaType mediaType) {
        super(attributeId, name, contentType, required);
        this.mediaType = mediaType;
    }
}
