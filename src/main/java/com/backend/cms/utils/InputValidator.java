package com.backend.cms.utils;


public class InputValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";

    public static boolean isValidEmail(String email) {
        // Email pattern validation
        return email != null && email.matches(EMAIL_REGEX);
    }
}