package org.pawlak.rentalApp;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.RentalDao;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.database.ConnectDB;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.service.BookService;
import org.pawlak.rentalApp.service.RentalService;
import org.pawlak.rentalApp.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        ConnectDB db = new ConnectDB();
        Connection connection = db.getConnection();

        if (connection != null) {
            BookDao bookDao = new BookDao(connection);
            UserDao userDao = new UserDao(connection);
            RentalDao rentalDao = new RentalDao(connection);

            RentalService rentalService = new RentalService(rentalDao, bookDao);

            List<Book> books = bookDao.findAll();
            List<User> users = userDao.findAll();

            if (books.isEmpty() && users.isEmpty()) {
                System.out.println("No books found");
                db.closeConnection();
                return;
            }

            Book bookToRent = books.get(0);
            User user = users.get(0);

            System.out.println("Wypożyczenie książek: " + bookToRent.getTitle());

            try{
                rentalService.rentBook(user, bookToRent);
                System.out.println("Książka została wypożyczona");
            } catch (IllegalStateException e) {
                System.out.println("Nie można wypożyczyć: " + e.getMessage());
            }

            // Pobierz aktywne wypożyczenia
            List<Rental> activeRentals = rentalService.getActiveRentals();
            if (!activeRentals.isEmpty()) {
                Rental rental = activeRentals.get(0);
                System.out.println("Zwracanie książki o ID wypożyczenia: " + rental.getId());
                rentalService.returnBook(rental.getId());
                System.out.println("Książka została zwrócona.");
            }

            db.closeConnection();

        } else {
            System.out.println("Connection error");
            return;
        }
    }
}