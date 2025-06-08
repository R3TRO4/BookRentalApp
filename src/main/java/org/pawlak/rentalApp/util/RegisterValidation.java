package org.pawlak.rentalApp.util;

import org.pawlak.rentalApp.model.enums.BookGenres;

import java.util.ArrayList;
import java.util.List;

public class RegisterValidation {

    public static List<String> validateEmail(String email, boolean emailTaken) {
        List<String> errors = new ArrayList<>();

        if (!email.contains("@")) {
            errors.add("Email address is invalid");
        }

        if (emailTaken) {
            errors.add("An account for the given email already exists");
        }

        return errors;
    }

    public static List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password.length() < 8) {
            errors.add("Password must be at least 8 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one digit");
        }

        return errors;
    }

    public static BookGenres validateGenre(String genreStr, List<String> errors) {
        try {
            return BookGenres.valueOf(genreStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Invalid book genre: " + genreStr);
            return null;
        }
    }
}
