package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateAttribute extends Attribute {
    private DateType dateType;

    public DateAttribute(String name, ContentType contentType, boolean required, DateType dateType) {
        super(name, contentType, required);
        this.dateType = dateType;
    }

}
