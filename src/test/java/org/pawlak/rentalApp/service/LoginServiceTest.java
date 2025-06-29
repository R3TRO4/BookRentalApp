package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class LoginServiceTest {

    private UserDao userDao;
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        userDao = mock(UserDao.class);
        loginService = new LoginService(userDao);
    }

    @Test
    void TC_043_shouldLoginWithCorrectCredentials() {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("Secret123", org.mindrot.jbcrypt.BCrypt.gensalt());
        User user = new User(1, "A", "mail@test.com", hashed, BookGenres.POETRY, UserRole.USER);
        when(userDao.findAll()).thenReturn(List.of(user));

        Optional<User> result = loginService.login("mail@test.com", "Secret123");

        assertThat(result).contains(user);
    }
}
