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
    private TextType type;

    public TextAttribute(String name, ContentType contentType, boolean required, int minimumLength, int maximumLength, TextType type) {
        super(name, contentType, required);
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.type = type;
    }
}
