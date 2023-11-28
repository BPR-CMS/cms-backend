package com.backend.cms.model;

public class AttributeFactory {
    public static TextAttribute createTextAttribute(String attributeId, String name, ContentType contentType, boolean required, Integer minimumLength, Integer maximumLength, boolean unique, TextType type, String defaultValue) {
        return new TextAttribute(attributeId, name, contentType, required, minimumLength, maximumLength, unique, type, defaultValue);
    }

    public static RichTextAttribute createRichTextAttribute(String attributeId, String name, ContentType contentType, boolean required, Integer minimumLength, Integer maximumLength, String defaultValue) {
        return new RichTextAttribute(attributeId, name, contentType, required, minimumLength, maximumLength, defaultValue);
    }

    public static MediaAttribute createMediaAttribute(String attributeId, String name, ContentType contentType, boolean required, MediaType mediaType) {
        return new MediaAttribute(attributeId, name, contentType, required, mediaType);
    }

    public static NumberAttribute createNumberAttribute(String attributeId, String name, ContentType contentType, boolean required, FormatType formatType, String defaultValue, boolean unique, Integer minimumValue, Integer maximumValue) {
        return new NumberAttribute(attributeId, name, contentType, required, formatType, defaultValue, unique, minimumValue, maximumValue);
    }

    public static DateAttribute createDateAttribute(String attributeId, String name, ContentType contentType, boolean required, DateType dateType, String defaultValue, boolean unique) {
        return new DateAttribute(attributeId, name, contentType, required, dateType, defaultValue, unique);
    }
}
