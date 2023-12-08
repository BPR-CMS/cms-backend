package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichTextAttribute extends Attribute {
    private Integer minimumLength;
    private Integer maximumLength;
    private String defaultValue;

    public RichTextAttribute(String attributeId, String name, ContentType contentType, boolean required, Integer minimumLength, Integer maximumLength, String defaultValue) {
        super(attributeId, name, contentType, required);
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.defaultValue = defaultValue;
    }
}