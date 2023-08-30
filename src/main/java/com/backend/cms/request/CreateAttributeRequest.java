package com.backend.cms.request;

import com.backend.cms.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateAttributeRequest {
    private String name;
    private ContentType contentType;
    private int minimumLength;
    private int maximumLength;
    private TextType type;
    private MediaType mediaType;
    private boolean required;
    private String format;
    private int defaultValue;
    private boolean unique;
    private int minimumValue;
    private int maximumValue;
    private DateType dateType;

}
