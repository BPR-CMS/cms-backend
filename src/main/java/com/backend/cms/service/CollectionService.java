package com.backend.cms.service;

import com.backend.cms.dto.CollectionDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.exceptions.UnsupportedContentTypeException;
import com.backend.cms.model.*;
import com.backend.cms.repository.CollectionRepository;
import com.backend.cms.request.*;
import com.backend.cms.utils.FieldCleaner;
import com.backend.cms.utils.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionService.class);


    public Collection findCollectionFailIfNotFound(String id) {
        Collection collection = collectionRepository.findByCollectionId(id);
        if (collection == null) throw new NotFoundException();

        return collection;
    }

    public Collection findCollectionByNameFailIfNotFound(String name) {
        Collection collection = collectionRepository.findByName(name);
        if (collection == null) throw new NotFoundException();

        return collection;
    }

    public String findNewId() {
        String id;
        do {
            id = Generator.generateId("c");
        } while (collectionRepository.findByCollectionId(id) != null);
        return id;
    }

    public void save(Collection collection) {
        if (collection != null) {
            collectionRepository.save(collection);
        }
    }

    public List<Collection> findAllCollections() {
        return collectionRepository.findAll();
    }

    public List<Collection> findCollectionByUserId(String userId) {
        return collectionRepository.findAllByUserId(userId);
    }

    public List<CollectionDTO> collectionToDTOs(List<Collection> collection) {
        return collection.stream().map(CollectionDTO::fromCollection).collect(Collectors.toList());
    }

    public void saveCollection(Collection collection, CreateCollectionRequest request) {

        try {

            Collection cleanedCollection = cleanCollectionFields(collection);
            String cleanedName = cleanCollectionName(request.getName());

            checkForDuplicateName(cleanedName);

            cleanedCollection.setName(cleanedName);
            save(cleanedCollection);
        } catch (NullPointerException ex) {
            // Handle the case where the name is null
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fields cannot be null.");
        }
    }

    private Collection cleanCollectionFields(Collection collection) {
        return FieldCleaner.cleanCollectionFields(collection);
    }

    private String cleanCollectionName(String name) {
        return (name != null) ? name.trim() : null;
    }


    private void checkForDuplicateName(String name) {
        Collection existingCollection = collectionRepository.findByName(name);

        if (existingCollection != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A collection with the same name already exists.");
        }
    }

    private void checkForDuplicateAttributeName(String collectionId, String attributeName) {
        Collection existingCollection = collectionRepository.findByCollectionId(collectionId);

        if (existingCollection != null) {
            // Iterate through the attributes of the existing collection
            for (Attribute existingAttribute : existingCollection.getAttributes()) {
                if (existingAttribute.getName().equals(attributeName)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "An attribute with the same name already exists in the collection.");
                }
            }
        }
    }

    public void addAttributeToCollection(String collectionId, Attribute attribute,
                                         CreateAttributeRequest request) {
        Optional<Collection> optionalCollection = Optional.ofNullable(collectionRepository.findByCollectionId(collectionId));
        if (optionalCollection.isPresent()) {
            Collection collection = optionalCollection.get();
checkForDuplicateAttributeName(collectionId,attribute.getName());
            // Set attribute-specific properties based on ContentType
            if (attribute instanceof TextAttribute && request.getContentType() == ContentType.TEXT) {
                setTextAttributeProperties((TextAttribute) attribute, request);
            } else if (attribute instanceof RichTextAttribute && request.getContentType() == ContentType.RICHTEXT) {
                setRichTextAttributeProperties((RichTextAttribute) attribute, request);
            } else if (attribute instanceof MediaAttribute && request.getContentType() == ContentType.MEDIA) {
                setMediaAttributeProperties((MediaAttribute) attribute, request);
            } else if (attribute instanceof NumberAttribute && request.getContentType() == ContentType.NUMBER) {
                setNumberAttributeProperties((NumberAttribute) attribute, request);
            } else if (attribute instanceof DateAttribute && request.getContentType() == ContentType.DATE) {
                setDateAttributeProperties((DateAttribute) attribute, request);
            }

            addAttributeToCollectionAndSave(collection, attribute);
        } else {
            throw new NotFoundException();
        }
    }

    private void setTextAttributeProperties(TextAttribute textAttribute, CreateAttributeRequest request) {
        textAttribute.setName(FieldCleaner.cleanField(request.getName()));
        textAttribute.setTextType(request.getTextType());
        textAttribute.setMinimumLength(request.getMinimumLength() != null ? request.getMinimumLength() : 0);
        textAttribute.setMaximumLength( request.getMaximumLength() != null ? request.getMaximumLength() : 0);
    }

    private void setRichTextAttributeProperties(RichTextAttribute richTextAttribute, CreateAttributeRequest request) {
        richTextAttribute.setName(FieldCleaner.cleanField(request.getName()));
        richTextAttribute.setMinimumLength(request.getMinimumLength() != null ? request.getMinimumLength() : 0);
        richTextAttribute.setMaximumLength(request.getMaximumRichTextLength() != null ? request.getMaximumRichTextLength() : 0);
    }

    private void setMediaAttributeProperties(MediaAttribute mediaAttribute, CreateAttributeRequest request) {
        mediaAttribute.setName(FieldCleaner.cleanField(request.getName()));
        mediaAttribute.setContentType(request.getContentType());
        mediaAttribute.setRequired(request.isRequired());
        mediaAttribute.setMediaType(request.getMediaType());
    }

    private void setNumberAttributeProperties(NumberAttribute numberAttribute, CreateAttributeRequest request) {
        numberAttribute.setName(FieldCleaner.cleanField(request.getName()));
        numberAttribute.setContentType(request.getContentType());
        numberAttribute.setRequired(request.isRequired());
        numberAttribute.setFormatType(request.getFormatType());
        numberAttribute.setDefaultValue(request.getDefaultValue());
        numberAttribute.setUnique(request.isUnique());
        numberAttribute.setMinimumValue( request.getMinimumValue() != null ? request.getMinimumValue() : 0);
        numberAttribute.setMaximumValue( request.getMaximumValue() != null ? request.getMaximumValue() : 0);
    }

    private void setDateAttributeProperties(DateAttribute dateAttribute, CreateAttributeRequest request) {
        dateAttribute.setName(FieldCleaner.cleanField(request.getName()));
        dateAttribute.setContentType(request.getContentType());
        dateAttribute.setRequired(request.isRequired());
        dateAttribute.setDateType(request.getDateType());
    }

    private void addAttributeToCollectionAndSave(Collection collection, Attribute attribute) {
        collection.getAttributes().add(attribute);
        collectionRepository.save(collection);
    }

    public Attribute createAttributeInstance(CreateAttributeRequest request) {
        ContentType contentType = request.getContentType();

        switch (contentType) {
            case TEXT:
                TextType textType = request.getTextType();
                if (textType == null) {
                    throw new IllegalArgumentException("textType is required for TextAttribute");
                }
                return AttributeFactory.createTextAttribute(
                        Generator.generateId("a"),
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getMinimumLength() != null ? request.getMinimumLength() : 0,
                        request.getMaximumLength() != null ? request.getMaximumLength() : 0,
                        request.isUnique(),
                        textType,
                        request.getDefaultValue());

            case RICHTEXT:
                return AttributeFactory.createRichTextAttribute(
                        Generator.generateId("a"),
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getMinimumLength() != null ? request.getMinimumLength() : 0,
                        request.getMaximumRichTextLength() != null ? request.getMaximumRichTextLength() : 0,
                        request.getDefaultValue());

            case MEDIA:
                MediaType mediaType = request.getMediaType();
                if (mediaType == null) {
                    throw new IllegalArgumentException("mediaType is required for MediaAttribute");
                }
                return AttributeFactory.createMediaAttribute(
                        Generator.generateId("a"),
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        mediaType);

            case NUMBER:
                FormatType formatType = request.getFormatType();
                if (formatType == null) {
                    throw new IllegalArgumentException("formatType is required for NumberAttribute");
                }
                return AttributeFactory.createNumberAttribute(
                        Generator.generateId("a"),
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        formatType,
                        request.getDefaultValue(),
                        request.isUnique(),
                        request.getMinimumValue() != null ? request.getMinimumValue() : 0,
                        request.getMaximumValue() != null ? request.getMaximumValue() : 0);

            case DATE:
                DateType dateType = request.getDateType();
                if (dateType == null) {
                    throw new IllegalArgumentException("dateType is required for DateAttribute");
                }
                return AttributeFactory.createDateAttribute(
                        Generator.generateId("a"),
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        dateType,
                        request.getDefaultValue(),
                        request.isUnique());

            default:
                throw new UnsupportedContentTypeException();
        }
    }
}
