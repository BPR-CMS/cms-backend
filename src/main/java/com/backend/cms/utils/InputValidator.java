package com.backend.cms.utils;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class InputValidator {

    private static final String NAME_REGEX = "^[a-zA-Z\\s]{2,20}$";


    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,16}$";


    public static void validatePassword(String password) {
        // Remove white spaces
        String trimmedPassword = password.trim();
        if (!isValidPassword(trimmedPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, """
                    Password must meet the following criteria:
                    - At least one digit.
                    - At least one lowercase letter.
                    - At least one uppercase letter.
                    - At least one special character from the set @#$%^&-+=().
                    - No whitespace allowed.
                    - Total length of the password should be between 8 and 20 characters.""");
        }
    }

    public static void validateName(String name) {
        // Remove white spaces
        String trimmedName = name.trim();

        if (!isValidName(trimmedName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name format. Name must be 2-20 characters long.");
        }
    }

    public static void validateEmail(String email) {
        // Remove white spaces
        String trimmedEmail = email.trim();
        if (!isValidEmail(trimmedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }

    private static boolean isValidPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    private static boolean isValidName(String name) {
        return name.matches(NAME_REGEX);
    }

    public static boolean isValidEmail(String email) {
        // Email pattern validation
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
    }

}