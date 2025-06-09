package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(
                new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER),
                new User(2, "Anna", "anna@test.com", "pass", BookGenres.FANTASY, UserRole.ADMIN)
        );
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        verify(userDao).findAll();
    }

    @Test
    void shouldReturnUserById() {
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        when(userDao.findById(1)).thenReturn(user);

        Optional<User> result = userService.getUserById(1);
        assertThat(result).contains(user);
    }

    @Test
    void shouldDetectIfEmailIsTaken() {
        User existingUser = new User(1, "A", "test@abc.com", "", BookGenres.POLITICAL, UserRole.USER);
        when(userDao.findAll()).thenReturn(List.of(existingUser));

        boolean result = userService.isEmailTaken("test@abc.com");

        assertThat(result).isTrue();
    }

    @Test
    void shouldRegisterNewUserWithValidData() {
        when(userDao.findAll()).thenReturn(List.of());

        userService.register("Jan", "jan@example.com", "Password1", "fantasy");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).insert(userCaptor.capture());

        User inserted = userCaptor.getValue();
        assertThat(inserted.getName()).isEqualTo("Jan");
        assertThat(inserted.getEmail()).isEqualTo("jan@example.com");
        assertThat(inserted.getFavoriteGenre()).isEqualTo(BookGenres.FANTASY);
        assertThat(inserted.getRole()).isEqualTo(UserRole.USER);
        assertThat(inserted.getPassword()).doesNotContain("Password1"); // powinno być zahashowane
    }

    @Test
    void shouldLoginWithCorrectCredentials() {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("Secret123", org.mindrot.jbcrypt.BCrypt.gensalt());
        User user = new User(1, "A", "mail@test.com", hashed, BookGenres.POETRY, UserRole.USER);
        when(userDao.findAll()).thenReturn(List.of(user));

        Optional<User> result = userService.login("mail@test.com", "Secret123");

        assertThat(result).contains(user);
    }

//    @Test
//    void shouldUpdateUserPasswordWhenValid() {
//        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
//
//        boolean result = userService.validateAndUpdatePassword(user, "NewPassword1");
//
//        assertThat(result).isTrue();
//        verify(userDao).update(user);
//    }

//    @Test
//    void shouldRejectInvalidPasswordChange() {
//        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
//        boolean result = userService.validateAndUpdatePassword(user, "abc");
//
//        assertThat(result).isFalse();
//        verify(userDao, never()).update(any());
//    }

//    @Test
//    void shouldAddUser() {
//        User newUser = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
//        userService.addUser(newUser);
//        verify(userDao).insert(newUser);
//    }

//    @Test
//    void shouldDeleteUser() {
//        userService.deleteUser(5);
//        verify(userDao).delete(5);
//    }
}
