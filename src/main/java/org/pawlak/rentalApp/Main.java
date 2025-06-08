package org.pawlak.rentalApp;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.RentalDao;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.database.ConnectDB;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.service.BookService;
import org.pawlak.rentalApp.service.RentalService;
import org.pawlak.rentalApp.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        ConnectDB db = new ConnectDB();
        Connection connection = db.getConnection();

        if (connection != null) {

            BookDao bookDao = new BookDao(connection);
            UserDao userDao = new UserDao(connection);
            RentalDao rentalDao = new RentalDao(connection, bookDao, userDao);

            BookService bookService = new BookService(bookDao);
            UserService userService = new UserService(userDao);
            RentalService rentalService = new RentalService(rentalDao, bookDao);


            while (true) {
                Optional<User> loggedUser = Optional.empty();

                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Rejestracja\n2. Logowanie\n3. Zakończ");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 1) {
                    System.out.println("Podaj imię:");
                    String name = scanner.nextLine();
                    System.out.println("Podaj email:");
                    String email = scanner.nextLine();
                    System.out.println("Podaj hasło:");
                    String password = scanner.nextLine();
                    System.out.println("Ulubiony gatunek:");
                    System.out.println("Dostępne gatunki to:");
                    for (BookGenres g : BookGenres.values()) {
                        System.out.println("- " + g.name());
                    }
                    String genre = scanner.nextLine();

                    try {
                        userService.register(name, email, password, genre);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Błąd: " + e.getMessage());
                    }

                } else if (choice == 2) {
                    System.out.println("Email: ");
                    String email = scanner.nextLine();
                    System.out.println("Hasło: ");
                    String password = scanner.nextLine();

                    loggedUser = userService.login(email, password);
                    if (loggedUser.isPresent()) {
                        System.out.println("Zalogowano jako: " + loggedUser.get().getName());
                    } else {
                        System.out.println("Błędny email lub hasło.");
                    }
                } else if (choice == 3) {
                    System.out.println("Do widzenia!");
                    return;
                } else {
                    System.out.println("Nieprawidłowy wybór.\n\n");
                }

                if (loggedUser.isPresent()) {
                    loggedInMenu: while (true) {
                        System.out.println("\nWybierz opcję:");
                        System.out.println("1. Wyświetl dostępne książki");
                        System.out.println("2. Wyświetl dostępne książki na podstawie ulubionego gatunku");
                        System.out.println("3. Wypożycz książkę");
                        System.out.println("4. Moje wypożyczenia");
                        System.out.println("5. Zwróć książkę");
                        System.out.println("6. Historia wypożyczeń");
                        System.out.println("7. Wyloguj");

                        String choice1 = scanner.nextLine();

                        switch (choice1) {
                            case "1":
                                List<Book> availableBooks = bookService.getAvailableBooks();
                                availableBooks.forEach(book ->
                                        System.out.println(
                                                book.getId() + ": " +
                                                        book.getTitle() +
                                                        "\nAutor: " + book.getAuthor() +
                                                        ",\nOpis: " + book.getDescription() +
                                                        "\nGatunek: " + book.getGenre() + "\n\n"));
                                break;

                            case "2":
                                List<Book> genreBooks = bookService.getAvailableBooksByUserFavoriteGenre(loggedUser.get());
                                if (genreBooks.isEmpty()) {
                                    System.out.println("Brak dostępnych książek w twoim ulubionym gatunku.");
                                } else {
                                    System.out.println("Dostępne książki w twoim ulubionym gatunku:");
                                    genreBooks.forEach(b -> System.out.println(
                                            b.getTitle() + " - " + b.getAuthor() +
                                                    "\n" + b.getDescription() + "\n\n"));
                                }
                                break;

                            case "3":
                                System.out.println("Podaj ID książki do wypożyczenia");
                                int bookId = Integer.parseInt(scanner.nextLine());
                                Optional<Book> bookOpt = bookService.getBookById(bookId);
                                if (bookOpt.isPresent() && bookOpt.get().isAvailable()) {
                                    rentalService.rentBook(loggedUser.get(), bookOpt.get());
                                    System.out.println("Wypożyczono książkę: " + bookOpt.get().getTitle());
                                } else {
                                    System.out.println("Książka jest już wypożyczona.");
                                }
                                break;

                            case "4":
                                List<Rental> rentals = rentalService.getActiveRentalsForUser(loggedUser.get());
                                if (rentals.isEmpty()) {
                                    System.out.println("Nie masz żadnych aktywnych wypożyczeń.");
                                } else {
                                    rentals.forEach(r -> System.out.println("Książka: " + r.getBook().getTitle() +
                                            ", wypożyczona: " + r.getRentalDate() + ", Do oddania przed: " + r.getDueDate()));
                                }
                                break;

                            case "5":
                                List<Rental> activeRentals = rentalService.getActiveRentalsForUser(loggedUser.get());
                                if (activeRentals.isEmpty()) {
                                    System.out.println("Nie masz żadnych aktywnych wypożyczeń.");
                                    break;
                                }

                                System.out.println("Twoje aktywne wypożyczenia:");
                                for (Rental r : activeRentals) {
                                    System.out.println("ID wypożyczenia: " + r.getId() + ", Książka " + r.getBook().getTitle());
                                }

                                System.out.println("Podaj ID wypożyczenia do zwrotu");
                                int rentalId = Integer.parseInt(scanner.nextLine());

                                try {
                                    rentalService.returnBook(rentalId);
                                    System.out.println("Ksiązka została zwrócona.");
                                } catch (Exception e) {
                                    System.out.println("Błąd przy zwrocie: " + e.getMessage());
                                }
                                break;

                            case "6":
                                List<Rental> history = rentalService.getRentalHistoryForUser(loggedUser.get());
                                if (history.isEmpty()) {
                                    System.out.println("Brak historii wypożyczeń.");
                                } else {
                                    System.out.println("Historia wypożyczeń:");
                                    for (Rental r : history) {
                                        System.out.println("Książka: " + r.getBook().getTitle() +
                                                ", \nAutor: " + r.getBook().getAuthor() +
                                                ", \nWypożyczona: " + r.getRentalDate() +
                                                ", \nZwrócona: " + r.getReturnDate());
                                    }
                                }
                                break;

                            case "7":
                                System.out.println("Wylogowano.\n\n");
                                break loggedInMenu;

                            default:
                                System.out.println("Nieprawidłowa opcja.\n\n");
                        }
                    }
                }
            }
        } else {
            System.out.println("Connection error");
            return;
        }
    }
}