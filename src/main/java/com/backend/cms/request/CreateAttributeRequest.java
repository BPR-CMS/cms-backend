package com.backend.cms.request;

import com.backend.cms.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAttributeRequest {
    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]{2,20}$", message = "Name must be 2-20 characters and contain only letters and spaces.")
    private String name;

    private ContentType contentType;

    @Min(value = 2, message = "Length must be at least 2.")
    private Integer minimumLength;

    @Max(value = 50, message = "Length cannot exceed 50.")
    private Integer maximumLength;

    @Max(value = 5000, message = "Length cannot exceed 5000.")
    private Integer maximumRichTextLength;

    private TextType textType;
    private MediaType mediaType;
    private boolean required;

    private FormatType formatType;
    private String defaultValue;
    private boolean unique;

    @Min(value = 0, message = "Value must be at least 0.")
    private Integer minimumValue;

    @Max(value = 40, message = "Value cannot exceed 40.")
    private Integer maximumValue;

    private DateType dateType;

    public void setName(String name) {
        this.name =  name != null ? name.trim() : null;
    }
}
