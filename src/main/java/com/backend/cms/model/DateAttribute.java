package com.backend.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateAttribute extends Attribute {
    private DateType dateType;
    private String defaultValue;
    private boolean unique;

    public DateAttribute(String attributeId, String name, ContentType contentType, boolean required, DateType dateType, String defaultValue, boolean unique) {
        super(attributeId, name, contentType, required);
        this.dateType = dateType;
        setDefaultValue(defaultValue,dateType);
        this.unique = unique;
    }

   public void setDefaultValue(String defaultValue, DateType dateType) {
    if (defaultValue == null || defaultValue.isEmpty()) {
        this.defaultValue = defaultValue;
        return;
    }

    try {
        DateFormat dateFormat;

        switch (dateType) {
            case DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case DATETIME:
                dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm");
                break;
            case TIME:
                dateFormat = new SimpleDateFormat("HH:mm");
                break;
            default:
                throw new IllegalArgumentException("Invalid DateType specified");
        }

        Date parsedDate = dateFormat.parse(defaultValue);
        this.defaultValue = dateFormat.format(parsedDate);
    } catch (ParseException e) {
        throw new IllegalArgumentException("Invalid date format for defaultValue");
    }
}
}
