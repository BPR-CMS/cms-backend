package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichTextAttribute extends Attribute {
    private int minimumLength;
    private int maximumLength;

    public RichTextAttribute(String name, ContentType contentType, boolean required, int minimumLength, int maximumLength) {
        super(name, contentType, required);
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
    }
}