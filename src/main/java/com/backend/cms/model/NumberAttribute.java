package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberAttribute extends Attribute {

    private String format;
    private int defaultValue;
    private boolean unique;
    private int minimumValue;
    private int maximumValue;

    public NumberAttribute(String name, ContentType contentType, boolean required, String format, int defaultValue, boolean unique, int minimumValue, int maximumValue) {
        super(name, contentType, required);
        this.format = format;
        this.defaultValue = defaultValue;
        this.unique = unique;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }
}
