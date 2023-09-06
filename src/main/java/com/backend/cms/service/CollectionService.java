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
        textAttribute.setType(request.getType());
        textAttribute.setMinimumLength(request.getMinimumLength());
        textAttribute.setMaximumLength(request.getMaximumLength());
    }

    private void setRichTextAttributeProperties(RichTextAttribute richTextAttribute, CreateAttributeRequest request) {
        richTextAttribute.setName(FieldCleaner.cleanField(request.getName()));
        richTextAttribute.setMinimumLength(request.getMinimumLength());
        richTextAttribute.setMaximumLength(request.getMaximumRichTextLength());
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
        numberAttribute.setFormat(request.getFormat());
        numberAttribute.setDefaultValue(request.getDefaultValue());
        numberAttribute.setUnique(request.isUnique());
        numberAttribute.setMinimumValue(request.getMinimumValue());
        numberAttribute.setMaximumValue(request.getMaximumValue());
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
                return AttributeFactory.createTextAttribute(
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getMinimumLength(),
                        request.getMaximumLength(),
                        request.getType());

            case RICHTEXT:
                return AttributeFactory.createRichTextAttribute(
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getMinimumLength(),
                        request.getMaximumRichTextLength());

            case MEDIA:
                return AttributeFactory.createMediaAttribute(
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getMediaType());

            case NUMBER:
                return AttributeFactory.createNumberAttribute(
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getFormat(),
                        request.getDefaultValue(),
                        request.isUnique(),
                        request.getMinimumValue(),
                        request.getMaximumValue());

            case DATE:
                return AttributeFactory.createDateAttribute(
                        request.getName(),
                        contentType,
                        request.isRequired(),
                        request.getDateType());

            default:
                throw new UnsupportedContentTypeException();
        }
    }
}
