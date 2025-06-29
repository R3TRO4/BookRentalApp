package org.pawlak.rentalApp.dao;

import org.junit.jupiter.api.*;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    void TC_011_shouldInsertAndFindUser() {
        User user = new User(0, "Jan", "jan@test.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        userDao.insert(user);

        List<User> allUsers = userDao.findAll();
        assertThat(allUsers).hasSize(1);
        assertThat(allUsers.getFirst().getName()).isEqualTo("Jan");
    }

    @Test
    void TC_012_shouldUpdateUser() {
        User user = new User(0, "Anna", "anna@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        userDao.insert(user);

        User inserted = userDao.findAll().getFirst();
        inserted.setName("Anna Nowak");
        userDao.update(inserted);

        User updated = userDao.findById(inserted.getId());
        assertThat(updated.getName()).isEqualTo("Anna Nowak");
    }

    //**************************************************************//
    //***********************Exemptions testing*********************//
    //**************************************************************//
    @Test
    void TC_013_insertShouldCatchSQLException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);

        // wymuszamy wyjątek przy executeUpdate()
        doThrow(new SQLException("DB error")).when(mockStmt).executeUpdate();

        UserDao userDao = new UserDao(mockConnection);
        User user = new User(0, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);

        // nie powinno rzucać wyjątku dalej, bo catchujemy w DAO
        userDao.insert(user);

        verify(mockStmt).executeUpdate();
    }

    @Test
    void TC_14_updateShouldCatchSQLException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
        doThrow(new SQLException("DB error")).when(mockStmt).executeUpdate();

        UserDao userDao = new UserDao(mockConnection);
        User user = new User(1, "Anna", "anna@test.com", "pass", BookGenres.FANTASY, UserRole.USER);

        userDao.update(user);

        verify(mockStmt).executeUpdate();
    }
}
