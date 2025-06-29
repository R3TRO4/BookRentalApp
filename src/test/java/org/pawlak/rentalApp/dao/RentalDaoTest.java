package org.pawlak.rentalApp.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RentalDaoTest {

    private Connection connection;
    private RentalDao rentalDao;
    private UserDao userDao;
    private BookDao bookDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, email VARCHAR, password VARCHAR, favorite_genre VARCHAR, role VARCHAR)");

        stmt.execute("CREATE TABLE books(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR, author VARCHAR, description VARCHAR, releaseYear INT, pageCount INT, genre VARCHAR, countOfRates INTEGER DEFAULT 0, sumOfRates INTEGER DEFAULT 0, available BOOLEAN, timesRented INTEGER DEFAULT 0)");

        stmt.execute("CREATE TABLE rentals(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INT, book_id INT, rental_date VARCHAR, due_date VARCHAR, return_date VARCHAR, penalty_fee)");

        userDao = new UserDao(connection);
        bookDao = new BookDao(connection);
        rentalDao = new RentalDao(connection, bookDao, userDao);
    }

    @Test
    void TC_015_shouldInsertAndUpdateRental() {
        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);

        userDao.insert(user);
        bookDao.insert(book);

        User insertedUser = userDao.findAll().getFirst();
        Book insertedBook = bookDao.findAll().getFirst();

        Rental rental = new Rental(0, insertedUser, insertedBook, LocalDate.now(), LocalDate.now().plusDays(30), null, 0);
        rentalDao.insert(rental);

        List<Rental> rentals = rentalDao.findAll();
        assertThat(rentals).hasSize(1);
        Rental insertedRental = rentals.getFirst();
        assertThat(insertedRental.getUser().getId()).isEqualTo(insertedUser.getId());
        assertThat(insertedRental.getBook().getId()).isEqualTo(insertedBook.getId());
        assertThat(insertedRental.getReturnDate()).isNull();

        insertedRental.setReturnDate(LocalDate.now());

        rentalDao.update(insertedRental);

        Rental updatedRental = rentalDao.findAll().getFirst();
        assertThat(updatedRental.getReturnDate()).isEqualTo(insertedRental.getReturnDate());
    }

    @Test
    void TC_016_shouldInsertRentalWithReturnDate() {
        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);

        userDao.insert(user);
        bookDao.insert(book);

        User insertedUser = userDao.findAll().getFirst();
        Book insertedBook = bookDao.findAll().getFirst();

        Rental rental = new Rental(0, insertedUser, insertedBook, LocalDate.now(), LocalDate.now().plusDays(30), LocalDate.now(), 0);

        rentalDao.insert(rental);

        List<Rental> rentals = rentalDao.findAll();
        assertThat(rentals).hasSize(1);
        assertThat(rentals.getFirst().getReturnDate()).isEqualTo(rental.getReturnDate());
    }

    @Test
    void TC_017_shouldUpdateRentalWithReturnDate() {
        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);

        userDao.insert(user);
        bookDao.insert(book);

        User insertedUser = userDao.findAll().getFirst();
        Book insertedBook = bookDao.findAll().getFirst();

        Rental rental = new Rental(0, insertedUser, insertedBook, LocalDate.now(), LocalDate.now().plusDays(30), null, 0);
        rentalDao.insert(rental);

        Rental insertedRental = rentalDao.findAll().getFirst();

        insertedRental.setUser(insertedUser);
        insertedRental.setBook(insertedBook);

        insertedRental.setReturnDate(LocalDate.now());

        rentalDao.update(insertedRental);

        Rental updatedRental = rentalDao.findAll().getFirst();
        assertThat(updatedRental.getReturnDate()).isNotNull();
    }

    @Test
    void TC_18_shouldUpdateRentalWithoutReturnDate() {
        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);

        userDao.insert(user);
        bookDao.insert(book);

        User insertedUser = userDao.findAll().getFirst();
        Book insertedBook = bookDao.findAll().getFirst();

        Rental rental = new Rental(0, insertedUser, insertedBook, LocalDate.now(), LocalDate.now().plusDays(30), null, 0);
        rentalDao.insert(rental);

        Rental insertedRental = rentalDao.findAll().getFirst();

        insertedRental.setUser(insertedUser);
        insertedRental.setBook(insertedBook);

        insertedRental.setReturnDate(null);

        rentalDao.update(insertedRental);

        Rental updatedRental = rentalDao.findAll().getFirst();
        assertThat(updatedRental.getReturnDate()).isNull();
    }

    @Test
    void TC_19_shouldInsertRentalWithoutReturnDate() {
        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);

        userDao.insert(user);
        bookDao.insert(book);

        User insertedUser = userDao.findAll().getFirst();
        Book insertedBook = bookDao.findAll().getFirst();

        Rental rental = new Rental(0, insertedUser, insertedBook, LocalDate.now(), LocalDate.now().plusDays(30), null, 0);

        rentalDao.insert(rental);

        List<Rental> rentals = rentalDao.findAll();
        assertThat(rentals).hasSize(1);
        assertThat(rentals.getFirst().getReturnDate()).isNull();
    }

    //**************************************************************//
    //***********************Exemptions testing*********************//
    //**************************************************************//
    @Test
    void TC_20_shouldNotThrowWhenInsertFailsDueToMissingTable() throws SQLException {
        connection.createStatement().execute("DROP TABLE rentals");

        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
        Rental rental = new Rental(0, user, book, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);

        rentalDao.insert(rental);
        assertThat(rentalDao.findAll()).isEmpty();
    }

    @Test
    void TC_21_shouldNotThrowWhenUpdateFailsDueToMissingTable() throws SQLException {
        connection.createStatement().execute("DROP TABLE rentals");

        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
        Rental rental = new Rental(999, user, book, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);

        rentalDao.update(rental);
    }

    @Test
    void TC_22_shouldNotThrowWhenInsertFailsDueToClosedConnection() throws SQLException {
        connection.close();

        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
        Rental rental = new Rental(0, user, book, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);

        rentalDao.insert(rental);
    }

    @Test
    void TC_23_shouldNotThrowWhenUpdateFailsDueToClosedConnection() throws SQLException {
        connection.close();

        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
        Rental rental = new Rental(999, user, book, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);

        rentalDao.update(rental);
    }
}