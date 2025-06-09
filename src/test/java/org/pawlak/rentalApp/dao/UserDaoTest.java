package org.pawlak.rentalApp.dao;

import org.junit.jupiter.api.*;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest {

    private Connection connection;
    private UserDao userDao;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT,
                password TEXT,
                favorite_genre TEXT,
                role TEXT
            )
        """);
        userDao = new UserDao(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.close();
    }

    @Test
    void shouldInsertAndFindUser() {
        User user = new User(0, "Jan", "jan@test.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        userDao.insert(user);

        List<User> allUsers = userDao.findAll();
        assertThat(allUsers).hasSize(1);
        assertThat(allUsers.get(0).getName()).isEqualTo("Jan");
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(0, "Anna", "anna@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        userDao.insert(user);

        User inserted = userDao.findAll().get(0);
        inserted.setName("Anna Nowak");
        userDao.update(inserted);

        User updated = userDao.findById(inserted.getId());
        assertThat(updated.getName()).isEqualTo("Anna Nowak");
    }
}
