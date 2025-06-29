package org.pawlak.rentalApp.service;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class RegisterServiceTest {
    private UserDao userDao;
    private RegisterService registerService;

    @BeforeEach
    public void setUp() {
        userDao = mock(UserDao.class);
        registerService = new RegisterService(userDao);
    }


    @Test
    void TC_056_shouldDetectIfEmailIsTaken() {
        User existingUser = new User(1, "Bartosz", "bartosz@example.com", "", BookGenres.POLITICAL, UserRole.USER);
        when(userDao.findAll()).thenReturn(List.of(existingUser));

        boolean result = registerService.isEmailTaken("bartosz@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void TC_057_shouldRegisterNewUserWithValidData() {
        when(userDao.findAll()).thenReturn(List.of());

        registerService.register("Bartosz", "bartosz@example.com", "Password1", "fantasy");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).insert(userCaptor.capture());

        User inserted = userCaptor.getValue();
        assertThat(inserted.getName()).isEqualTo("Bartosz");
        assertThat(inserted.getEmail()).isEqualTo("bartosz@example.com");
        AssertionsForInterfaceTypes.assertThat(inserted.getFavoriteGenre()).isEqualTo(BookGenres.FANTASY);
        AssertionsForInterfaceTypes.assertThat(inserted.getRole()).isEqualTo(UserRole.USER);
        assertThat(inserted.getPassword()).doesNotContain("Password1"); // powinno być zahashowane
    }

    @Test
    void TC_058_shouldNotUpdatePasswordWhenInvalid() {
        User user = new User(1, "Bartosz", "bartosz@test.com", "pass", BookGenres.FANTASY, UserRole.USER);

        boolean result = registerService.validateAndUpdatePassword(user, "123"); // za krótkie, niepoprawne

        assertThat(result).isFalse();
        verify(userDao, never()).update(any());
    }

    @Test
    void TC_059_shouldUpdatePasswordWhenValid() {
        User user = new User(1, "Bartosz", "bartosz@test.com", "oldHashedPass", BookGenres.FANTASY, UserRole.USER);

        boolean result = registerService.validateAndUpdatePassword(user, "NewStrongPass123");

        assertThat(result).isTrue();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getPassword()).doesNotContain("NewStrongPass123"); // hasło powinno być zahashowane
        assertThat(updatedUser.getPassword()).isNotEqualTo("oldHashedPass");
    }

    @Test
    void TC_060_shouldNotUpdatePasswordIfInvalid() {
        User user = new User(1, "Test", "test@test.com", "oldHash", BookGenres.SCIENCE_FICTION, UserRole.USER);

        // Przykład: hasło zbyt krótkie lub bez cyfry itp.
        boolean result = registerService.validateAndUpdatePassword(user, "short");

        assertThat(result).isFalse();
        verify(userDao, never()).update(any());
    }

    @Test
    void TC_061_shouldNotRegisterUserIfEmailIsInvalid() {
        when(userDao.findAll()).thenReturn(List.of());

        registerService.register("Bartosz", "invalid-email", "Password1", "fantasy");

        verify(userDao, never()).insert(any());
    }
}
