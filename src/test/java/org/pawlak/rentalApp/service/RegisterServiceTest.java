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
    void shouldDetectIfEmailIsTaken() {
        User existingUser = new User(1, "A", "test@abc.com", "", BookGenres.POLITICAL, UserRole.USER);
        when(userDao.findAll()).thenReturn(List.of(existingUser));

        boolean result = registerService.isEmailTaken("test@abc.com");

        assertThat(result).isTrue();
    }

    @Test
    void shouldRegisterNewUserWithValidData() {
        when(userDao.findAll()).thenReturn(List.of());

        registerService.register("Jan", "jan@example.com", "Password1", "fantasy");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).insert(userCaptor.capture());

        User inserted = userCaptor.getValue();
        assertThat(inserted.getName()).isEqualTo("Jan");
        assertThat(inserted.getEmail()).isEqualTo("jan@example.com");
        AssertionsForInterfaceTypes.assertThat(inserted.getFavoriteGenre()).isEqualTo(BookGenres.FANTASY);
        AssertionsForInterfaceTypes.assertThat(inserted.getRole()).isEqualTo(UserRole.USER);
        assertThat(inserted.getPassword()).doesNotContain("Password1"); // powinno być zahashowane
    }

    @Test
    void shouldNotUpdatePasswordWhenInvalid() {
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);

        boolean result = registerService.validateAndUpdatePassword(user, "123"); // za krótkie, niepoprawne

        assertThat(result).isFalse();
        verify(userDao, never()).update(any());
    }

    @Test
    void shouldUpdatePasswordWhenValid() {
        User user = new User(1, "Jan", "jan@test.com", "oldHashedPass", BookGenres.FANTASY, UserRole.USER);

        boolean result = registerService.validateAndUpdatePassword(user, "NewStrongPass123");

        assertThat(result).isTrue();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getPassword()).doesNotContain("NewStrongPass123"); // hasło powinno być zahashowane
        assertThat(updatedUser.getPassword()).isNotEqualTo("oldHashedPass");
    }

    @Test
    void shouldNotUpdatePasswordIfInvalid() {
        User user = new User(1, "Test", "test@test.com", "oldHash", BookGenres.SCIENCE_FICTION, UserRole.USER);

        // Przykład: hasło zbyt krótkie lub bez cyfry itp.
        boolean result = registerService.validateAndUpdatePassword(user, "short");

        assertThat(result).isFalse();
        verify(userDao, never()).update(any());
    }

    @Test
    void shouldNotRegisterUserIfEmailIsInvalid() {
        when(userDao.findAll()).thenReturn(List.of());

        registerService.register("Jan", "invalid-email", "Password1", "fantasy");

        verify(userDao, never()).insert(any());
    }
}
