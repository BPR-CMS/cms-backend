package com.backend.cms.utils;

import com.backend.cms.model.User;


// Utility class  for cleaning the fields of a User object by trimming and removing extra spaces.
public class FieldCleaner {

    public static User cleanUserFields(User user) {
        user.setFirstName(cleanField(user.getFirstName()));
        user.setLastName(cleanField(user.getLastName()));
        user.setEmail(cleanField(user.getEmail()));
        user.setPassword(cleanField(user.getPassword()));
        return user;
    }

    public static String cleanField(String field) {
        return field.trim().replaceAll("\\s+", " ");
    }
}
