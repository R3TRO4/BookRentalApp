package org.pawlak.rentalApp.util;

import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.model.enums.BookGenres;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterValidationTest {

    @Test
    void TC_085_shouldReturnErrorIfEmailInvalid() {
        List<String> errors = RegisterValidation.validateEmail("invalidEmail", false);
        assertThat(errors).contains("Email address is invalid");
    }

    @Test
    void TC_086_shouldReturnErrorIfEmailTaken() {
        List<String> errors = RegisterValidation.validateEmail("test@example.com", true);
        assertThat(errors).contains("An account for the given email already exists");
    }

    @Test
    void TC_087_shouldValidatePasswordCorrectly() {
        List<String> errors = RegisterValidation.validatePassword("abc");
        assertThat(errors)
                .contains("Password must be at least 8 characters")
                .contains("Password must contain at least one uppercase letter")
                .contains("Password must contain at least one digit");
    }

    @Test
    void TC_088_shouldReturnEmptyListIfPasswordValid() {
        List<String> errors = RegisterValidation.validatePassword("Secure123");
        assertThat(errors).isEmpty();
    }

    @Test
    void TC_089_shouldReturnGenreIfValid() {
        List<String> errors = new ArrayList<>();
        BookGenres genre = RegisterValidation.validateGenre("fantasy", errors);
        assertThat(genre).isEqualTo(BookGenres.FANTASY);
        assertThat(errors).isEmpty();
    }

    @Test
    void TC_090_shouldAcceptValidGenre() {
        List<String> errors = new ArrayList<>();
        BookGenres genre = RegisterValidation.validateGenre("OTHER", errors);
        assertThat(genre).isEqualTo(BookGenres.OTHER);
        assertThat(errors).isEmpty();
    }

    @Test
    void TC_091_shouldReturnErrorIfGenreInvalid() {
        List<String> errors = new ArrayList<>();
        BookGenres genre = RegisterValidation.validateGenre("unknown_genre", errors);
        assertThat(genre).isEqualTo(BookGenres.UNKNOWN);
        assertThat(errors).contains("Invalid book genre: unknown_genre");
    }
}
