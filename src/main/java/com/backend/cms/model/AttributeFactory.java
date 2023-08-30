package com.backend.cms.model;

public class AttributeFactory {
    public static TextAttribute createTextAttribute(String name, ContentType contentType, boolean required, int minimumLength, int maximumLength, TextType type) {
        return new TextAttribute(name, contentType, required, minimumLength, maximumLength, type);
    }

    public static RichTextAttribute createRichTextAttribute(String name, ContentType contentType, boolean required, int minimumLength, int maximumLength) {
        return new RichTextAttribute(name, contentType, required, minimumLength, maximumLength);
    }

    public static MediaAttribute createMediaAttribute(String name, ContentType contentType, boolean required, MediaType mediaType) {
        return new MediaAttribute(name, contentType, required, mediaType);
    }

    public static NumberAttribute createNumberAttribute(String name, ContentType contentType, boolean required, String format, int defaultValue, boolean unique, int minimumValue, int maximumValue) {
        return new NumberAttribute(name, contentType, required, format, defaultValue, unique, minimumValue, maximumValue);
    }

    public static DateAttribute createDateAttribute(String name, ContentType contentType, boolean required, DateType dateType) {
        return new DateAttribute(name, contentType, required, dateType);
    }
}
