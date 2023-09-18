package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextAttribute extends Attribute {
    private int minimumLength;
    private int maximumLength;
    private boolean unique;
    private TextType textType;
    private String defaultValue;

    public TextAttribute(String attributeId, String name, ContentType contentType, boolean required, int minimumLength, int maximumLength, boolean unique, TextType textType, String defaultValue) {
        super(attributeId, name, contentType, required);
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.unique = unique;
        this.textType = textType;
        this.defaultValue = defaultValue;
    }
}
