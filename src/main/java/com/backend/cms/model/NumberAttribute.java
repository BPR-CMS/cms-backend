package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberAttribute extends Attribute {

    private FormatType formatType;
    private String defaultValue;
    private boolean unique;
    private int minimumValue;
    private int maximumValue;

    public NumberAttribute(String attributeId, String name, ContentType contentType, boolean required, FormatType formatType, String defaultValue, boolean unique, int minimumValue, int maximumValue) {
        super(attributeId, name, contentType, required);
        this.formatType = formatType;
        this.defaultValue = defaultValue;
        this.unique = unique;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }
}
