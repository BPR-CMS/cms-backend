package com.backend.cms.utils;

// Utility class for generating unique IDs
public final class Generator {

    // Generates a random alphanumeric string of the specified length
    private static String generateId(int length) {
        String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        while (length-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    // Generates a unique ID with a prefix and a random alphanumeric string of length 5
    public static String generateId(String prefix) {
        return prefix + generateId(5);
    }
}