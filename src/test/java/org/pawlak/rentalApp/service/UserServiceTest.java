package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void TC_77_shouldReturnAllUsers() {
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
    void TC_78_shouldReturnUserById() {
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        when(userDao.findById(1)).thenReturn(user);

        Optional<User> result = userService.getUserById(1);
        assertThat(result).contains(user);
    }
}
