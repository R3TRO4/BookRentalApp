package org.pawlak.rentalApp.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.enums.BookGenres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class BookDaoTest {

    private Connection connection;
    private BookDao bookDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE books(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR NOT NULL, author VARCHAR NOT NULL, description VARCHAR, releaseYear INT, pageCount INT, genre VARCHAR, countOfRates INTEGER DEFAULT 0, sumOfRates INTEGER DEFAULT 0, available BOOLEAN, timesRented INTEGER DEFAULT 0)");

        bookDao = new BookDao(connection);
    }

    @Test
    void TC_001_shouldInsertAndUpdateBook() {
        // GIVEN - pusta tabela books w bazie danych.

        // WHEN
        Book book = new Book(0, "Test Title", "Author Name", "Description", 2021, 250, BookGenres.FANTASY, 0, 0, true, 0);

        bookDao.insert(book);

        List<Book> books = bookDao.findAll();

        // THEN
        assertThat(books).hasSize(1); // Jedna książka w bazie
        Book inserted = books.getFirst();
        assertThat(inserted.getTitle()).isEqualTo("Test Title"); // Czy tytuł jak zakładny
        assertThat(inserted.isAvailable()).isTrue(); // Czy dostępna

        inserted.setAvailable(false); // Ustawienie dostępności na false
        bookDao.update(inserted);

        List<Book> updatedBooks = bookDao.findAll();
        assertThat(updatedBooks.getFirst().isAvailable()).isFalse(); // Czy niedostępna
    }

    @Test
    void TC_002_shouldGetAvailableBooks() {
        // GIVEN
        Book book1 = new Book(0, "Available Book", "Author A", "Desc A", 2020, 100, BookGenres.FANTASY, 0, 0, true, 0);
        Book book2 = new Book(0, "Unavailable Book", "Author B", "Desc B", 2019, 150, BookGenres.SCIENCE_FICTION, 0, 0, false, 0);

        bookDao.insert(book1);
        bookDao.insert(book2);

        // WHEN
        List<Book> availableBooks = bookDao.getAvailableBooks();

        // THEN
        assertThat(availableBooks).hasSize(1);
        assertThat(availableBooks.getFirst().getTitle()).isEqualTo("Available Book");
    }

    @Test
    void TC_003_shouldFindAvailableBooksByGenre() {
        // GIVEN
        Book book1 = new Book(0, "Fantasy Book 1", "Author X", "Desc X", 2018, 300, BookGenres.FANTASY, 0, 0, true, 0);
        Book book2 = new Book(0, "Fantasy Book 2", "Author Y", "Desc Y", 2017, 200, BookGenres.FANTASY, 0, 0, false, 0);
        Book book3 = new Book(0, "Drama Book", "Author Z", "Desc Z", 2016, 150, BookGenres.DRAMA, 0, 0, true, 0);

        bookDao.insert(book1);
        bookDao.insert(book2);
        bookDao.insert(book3);

        // WHEN
        List<Book> fantasyAvailable = bookDao.findAvailableBooksByGenre(BookGenres.FANTASY.name());

        // THEN
        assertThat(fantasyAvailable).hasSize(1);
        assertThat(fantasyAvailable.getFirst().getTitle()).isEqualTo("Fantasy Book 1");
    }

    //**************************************************************//
    //***********************Exemptions testing*********************//
    //**************************************************************//
    @Test
    void TC_004_shouldThrowExceptionWhenInsertingToNonExistentTable() throws SQLException {
        // GIVEN
        connection.createStatement().execute("DROP TABLE books");

        // WHEN
        Book book = new Book(0, "Test", "Author", "Desc", 2022, 123, BookGenres.FANTASY, 0, 0, true, 0);

        // THEN
        assertThatThrownBy(() -> bookDao.insert(book))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void TC_005_shouldThrowExceptionWhenUpdatingToNonExistentTable() throws SQLException {
        // GIVEN
        connection.createStatement().execute("DROP TABLE books");

        // WHEN
        Book book = new Book(1, "Test", "Author", "Desc", 2022, 123, BookGenres.FANTASY, 0, 0, true, 0);

        // THEN
        assertThatThrownBy(() -> bookDao.update(book))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void TC_006_shouldThrowExceptionWhenInsertingNullTitle() {
        // GIVEN - Pusta tabela

        // WHEN
        Book book = new Book(0, null, "Author", "Desc", 2022, 123, BookGenres.FANTASY, 0, 0, true, 0);

        // THEN
        assertThatThrownBy(() -> bookDao.insert(book))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void TC_007_shouldThrowExceptionWhenConnectionIsClosedOnInsert() throws SQLException {
        // GIVEN
        connection.close();

        // WHEN
        Book book = new Book(0, "Test", "Author", "Desc", 2022, 123, BookGenres.FANTASY, 0, 0, true, 0);

        // THEN
        assertThatThrownBy(() -> bookDao.insert(book))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void TC_008_shouldThrowExceptionWhenConnectionIsClosedOnUpdate() throws SQLException {
        // GIVEN
        connection.close();

        // WHEN
        Book book = new Book(1, "Test", "Author", "Desc", 2022, 123, BookGenres.FANTASY, 0, 0, true, 0);

        // THEN
        assertThatThrownBy(() -> bookDao.update(book))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void TC_009_shouldThrowExceptionWhenGettingAvailableBooksFromNonExistentTable() throws SQLException {
        // GIVEN
        connection.createStatement().execute("DROP TABLE books");

        // WHEN & THEN
        assertThatThrownBy(() -> bookDao.getAvailableBooks())
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }

    @Test
    void TC_010_shouldThrowExceptionWhenFindingAvailableBooksByGenreFromNonExistentTable() throws SQLException {
        // GIVEN
        connection.createStatement().execute("DROP TABLE books");

        // WHEN & THEN
        assertThatThrownBy(() -> bookDao.findAvailableBooksByGenre(BookGenres.FANTASY.name()))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class);
    }
}
