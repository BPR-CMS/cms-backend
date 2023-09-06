package com.backend.cms.model;

public class AttributeFactory {
    public static TextAttribute createTextAttribute(String attributeId, String name, ContentType contentType, boolean required, int minimumLength, int maximumLength, TextType type) {
        return new TextAttribute(attributeId, name, contentType, required, minimumLength, maximumLength, type);
    }

    public static RichTextAttribute createRichTextAttribute(String attributeId, String name, ContentType contentType, boolean required, int minimumLength, int maximumLength) {
        return new RichTextAttribute(attributeId, name, contentType, required, minimumLength, maximumLength);
    }

    public static MediaAttribute createMediaAttribute(String attributeId, String name, ContentType contentType, boolean required, MediaType mediaType) {
        return new MediaAttribute(attributeId, name, contentType, required, mediaType);
    }

    public static NumberAttribute createNumberAttribute(String attributeId, String name, ContentType contentType, boolean required, String format, int defaultValue, boolean unique, int minimumValue, int maximumValue) {
        return new NumberAttribute(attributeId, name, contentType, required, format, defaultValue, unique, minimumValue, maximumValue);
    }

    public static DateAttribute createDateAttribute(String attributeId, String name, ContentType contentType, boolean required, DateType dateType) {
        return new DateAttribute(attributeId, name, contentType, required, dateType);
    }
}
