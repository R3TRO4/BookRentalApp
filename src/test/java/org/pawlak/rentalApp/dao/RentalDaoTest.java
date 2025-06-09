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
                "title VARCHAR, author VARCHAR, description VARCHAR, releaseYear INT, pageCount INT, genre VARCHAR, available BOOLEAN)");

        stmt.execute("CREATE TABLE rentals(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INT, book_id INT, rental_date VARCHAR, due_date VARCHAR, return_date VARCHAR)");

        userDao = new UserDao(connection);
        bookDao = new BookDao(connection);
        rentalDao = new RentalDao(connection, bookDao, userDao);
    }

    @Test
    void shouldInsertAndUpdateRental() {
        // Przygotuj user i book
        User user = new User(0, "Jan Kowalski", "jan@example.com", "hashedPass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(0, "Władca Pierścieni", "Tolkien", "Epicka opowieść", 1954, 500, BookGenres.FANTASY, true);

        userDao.insert(user);
        bookDao.insert(book);

        // Pobierz user i book z bazy by mieć id
        User insertedUser = userDao.findAll().get(0);
        Book insertedBook = bookDao.findAll().get(0);

        // Insert rental
        Rental rental = new Rental(0, insertedUser, insertedBook, LocalDate.now(), LocalDate.now().plusDays(30), null);
        rentalDao.insert(rental);

        // Pobierz wstawiony rental i sprawdź
        List<Rental> rentals = rentalDao.findAll();
        assertThat(rentals).hasSize(1);
        Rental insertedRental = rentals.get(0);
        assertThat(insertedRental.getUser().getId()).isEqualTo(insertedUser.getId());
        assertThat(insertedRental.getBook().getId()).isEqualTo(insertedBook.getId());
        assertThat(insertedRental.getReturnDate()).isNull();

        // Zaktualizuj zwrot daty
        insertedRental.setReturnDate(LocalDate.now());

        rentalDao.update(insertedRental);

        // Pobierz ponownie i sprawdź update
        Rental updatedRental = rentalDao.findAll().get(0);
        assertThat(updatedRental.getReturnDate()).isEqualTo(insertedRental.getReturnDate());
    }
}
