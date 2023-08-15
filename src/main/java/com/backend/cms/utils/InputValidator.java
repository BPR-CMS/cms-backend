package com.backend.cms.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class InputValidator {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 16;

    public static void validatePassword(String password) {
        if (!isValidPassword(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be 8-16 characters long.");
        }
    }

    public static void validateName(String name) {
        if (!isValidName(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name format. Name must be 2-20 characters long.");
        }
    }

    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }

    private static boolean isValidPassword(String password) {
        return StringUtils.hasLength(password) &&
                password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
    }

    private static boolean isValidName(String name) {
        return StringUtils.hasLength(name) &&
                name.length() >= MIN_NAME_LENGTH && name.length() <= MAX_NAME_LENGTH;
    }

    private static boolean isValidEmail(String email) {
        // Basic email pattern validation (modify as needed)
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
    }

}
