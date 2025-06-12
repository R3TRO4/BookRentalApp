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

class BookDaoTest {

    private Connection connection;
    private BookDao bookDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE books(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR, author VARCHAR, description VARCHAR, releaseYear INT, pageCount INT, genre VARCHAR, countOfRates INTEGER DEFAULT 0, sumOfRates INTEGER DEFAULT 0, available BOOLEAN)");

        bookDao = new BookDao(connection);
    }

    @Test
    void shouldInsertAndUpdateBook() {
        Book book = new Book(0, "Test Title", "Author Name", "Description", 2021, 250, BookGenres.FANTASY, 0, 0, true);

        bookDao.insert(book);

        List<Book> books = bookDao.findAll();
        assertThat(books).hasSize(1);
        Book inserted = books.get(0);
        assertThat(inserted.getTitle()).isEqualTo("Test Title");
        assertThat(inserted.isAvailable()).isTrue();

        // Update availability
        inserted.setAvailable(false);
        bookDao.update(inserted);

        List<Book> updatedBooks = bookDao.findAll();
        assertThat(updatedBooks.get(0).isAvailable()).isFalse();
    }

    @Test
    void shouldGetAvailableBooks() {
        Book book1 = new Book(0, "Available Book", "Author A", "Desc A", 2020, 100, BookGenres.FANTASY,  0, 0, true);
        Book book2 = new Book(0, "Unavailable Book", "Author B", "Desc B", 2019, 150, BookGenres.SCIENCE_FICTION,  0, 0, false);

        bookDao.insert(book1);
        bookDao.insert(book2);

        List<Book> availableBooks = bookDao.getAvailableBooks();
        assertThat(availableBooks).hasSize(1);
        assertThat(availableBooks.get(0).getTitle()).isEqualTo("Available Book");
    }

    @Test
    void shouldFindAvailableBooksByGenre() {
        Book book1 = new Book(0, "Fantasy Book 1", "Author X", "Desc X", 2018, 300, BookGenres.FANTASY,  0, 0, true);
        Book book2 = new Book(0, "Fantasy Book 2", "Author Y", "Desc Y", 2017, 200, BookGenres.FANTASY,  0, 0, false);
        Book book3 = new Book(0, "Drama Book", "Author Z", "Desc Z", 2016, 150, BookGenres.DRAMA,  0, 0, true);

        bookDao.insert(book1);
        bookDao.insert(book2);
        bookDao.insert(book3);

        List<Book> fantasyAvailable = bookDao.findAvailableBooksByGenre(BookGenres.FANTASY.name());
        assertThat(fantasyAvailable).hasSize(1);
        assertThat(fantasyAvailable.get(0).getTitle()).isEqualTo("Fantasy Book 1");
    }
}
