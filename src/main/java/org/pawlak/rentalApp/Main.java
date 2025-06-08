package org.pawlak.rentalApp;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.RentalDao;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.database.ConnectDB;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;
import org.pawlak.rentalApp.service.BookService;
import org.pawlak.rentalApp.service.RentalService;
import org.pawlak.rentalApp.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.*;

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
                        continue;
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
                        User user = loggedUser.get();

                        if (user.getRole() == UserRole.ADMIN && Objects.equals(user.getPassword(), "$2a$10$1Jbj73quvHUU1wul1j4OD.kQnZs4Psemp.aw7ttPrxm9eAYvaJJXC")) {
                            System.out.println("Pierwsze logowanie jako administrator. Ustaw nowe hasło:");
                            String newPassword = scanner.nextLine();

                            if (userService.validateAndUpdatePassword(user, newPassword)) {
                                loggedUser = Optional.of(user); // logujemy po zmianie hasła
                            } else {
                                System.out.println("Spróbuj ponownie.");
                                break; // przerwij, bo hasło nie przeszło walidacji
                            }
                        }

                        System.out.println("Zalogowano jako: " + loggedUser.get().getName());
                    } else {
                        System.out.println("Błędny email lub hasło.");
                    }
                } else if (choice == 3) {
                    db.closeConnection();
                    System.out.println("Do widzenia!");
                    return;
                } else {
                    System.out.println("Nieprawidłowy wybór.\n\n");
                }

                if (loggedUser.isPresent() && loggedUser.get().getRole() == UserRole.ADMIN) {
                    loggedInMenu: while (true) {
                        System.out.println("ADMIN PANEL:");
                        System.out.println("1. Dodaj książkę");
                        System.out.println("2. Edytuj książkę");
                        System.out.println("3. Usuń książkę");
                        System.out.println("4. Wyświetl wszystkich użytkowników");
                        System.out.println("5. Wyloguj");

                        String adminChoice = scanner.nextLine();

                        switch (adminChoice) {
                            case "1":
                                if (loggedUser.get().getRole() != UserRole.ADMIN) {
                                    System.out.println("Brak uprawnień. Tylko administrator ma dostęp do tej funkcji.");
                                    break;
                                }

                                System.out.println("Tytuł:");
                                String title = scanner.nextLine();
                                System.out.println("Autor:");
                                String author = scanner.nextLine();
                                System.out.println("Opis:");
                                String description = scanner.nextLine();
                                System.out.println("Rok wydania: ");
                                int releaseYear = Integer.parseInt(scanner.nextLine());
                                System.out.println("Ilość stron: ");
                                int pageCount = Integer.parseInt(scanner.nextLine());
                                System.out.println("Gatunek:");
                                for (BookGenres g : BookGenres.values()) {
                                    System.out.println("- " + g.name());
                                }
                                String genreStr = scanner.nextLine();


                                try {
                                    BookGenres genre = BookGenres.valueOf(genreStr.toUpperCase());
                                    Book book = new Book(0, title, author, description, releaseYear, pageCount, genre, true);
                                    bookService.addBook(book);
                                    System.out.println("Dodano książkę.");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Niepoprawny gatunek.");
                                }
                                break;

                            case "2":
                                if (loggedUser.get().getRole() != UserRole.ADMIN) {
                                    System.out.println("Brak uprawnień. Tylko administrator ma dostęp do tej funkcji.");
                                    break;
                                }

                                System.out.println("Podaj ID książki do edycji:");
                                int editId = Integer.parseInt(scanner.nextLine());
                                Optional<Book> bookOpt = bookService.getBookById(editId);
                                if (bookOpt.isPresent()) {
                                    Book b = bookOpt.get();

                                    System.out.println("Nowy tytuł [" + b.getTitle() + "]: ");
                                    String newTitle = scanner.nextLine();
                                    if (!newTitle.isBlank()) {
                                        b.setTitle(newTitle);
                                    }

                                    System.out.println("Nowy autor [" + b.getAuthor() + "]: ");
                                    String newAuthor = scanner.nextLine();
                                    if (!newAuthor.isBlank()) {
                                        b.setAuthor(newAuthor);
                                    }

                                    System.out.println("Nowy opis: ");
                                    String newDescription = scanner.nextLine();
                                    if (!newDescription.isBlank()) {
                                        b.setDescription(newDescription);
                                    }

                                    System.out.println("Nowy rok wydania: ");
                                    String newReleaseYear = scanner.nextLine();
                                    if (!newReleaseYear.isBlank()) {
                                        try{
                                            int newReleaseYearInt = Integer.parseInt(newReleaseYear);
                                            b.setReleaseYear(newReleaseYearInt);
                                        } catch (NumberFormatException e) {
                                            System.out.println("Nieprawidłowa liczba. Zmiana została pominięta");
                                        }
                                    }

                                    System.out.println("Nowy rok wydania: ");
                                    String newPageCount = scanner.nextLine();
                                    if (!newPageCount.isBlank()) {
                                        try{
                                            int newPageCountInt = Integer.parseInt(newPageCount);
                                            b.setPageCount(newPageCountInt);
                                        } catch (NumberFormatException e) {
                                            System.out.println("Nieprawidłowa liczba. Zmiana została pominięta");
                                        }
                                    }

                                    System.out.println("Nowy gatunek: ");
                                    System.out.println("Dostępne gatunki to:");
                                    for (BookGenres g : BookGenres.values()) {
                                        System.out.println("- " + g.name());
                                    }
                                    String newGenreStr = scanner.nextLine();
                                    if(!newGenreStr.isBlank()) {
                                        try {
                                            BookGenres newGenre = BookGenres.valueOf(newGenreStr.toUpperCase());
                                            b.setGenre(newGenre);
                                        } catch (IllegalArgumentException e) {
                                            System.out.println(("Invalid book genre: " + e.getMessage()));
                                        }
                                    }

                                    bookService.updateBook(b);
                                    System.out.println("Zaktualizowano książkę.");
                                } else {
                                    System.out.println("Nie znaleziono książki.");
                                }
                                break;

                            case "3":
                                if (loggedUser.get().getRole() != UserRole.ADMIN) {
                                    System.out.println("Brak uprawnień. Tylko administrator ma dostęp do tej funkcji.");
                                    break;
                                }

                                System.out.println("Podaj ID książki do usunięcia:");
                                int deleteId = Integer.parseInt(scanner.nextLine());
                                bookService.deleteBook(deleteId);
                                System.out.println("Usunięto książkę.");
                                break;

                            case "4":
                                if (loggedUser.get().getRole() != UserRole.ADMIN) {
                                    System.out.println("Brak uprawnień. Tylko administrator ma dostęp do tej funkcji.");
                                    break;
                                }

                                List<User> allUsers = userService.getAllUsers();
                                for (User u : allUsers) {
                                    int activeCount = rentalService.getActiveRentalsForUser(u).size();
                                    System.out.println(u.getId() + ": " + u.getName() + " - " + u.getEmail() + " | Aktywne wypożyczenia: " + activeCount);                                }
                                break;

                            case "5":
                                System.out.println("Wylogowano.\n\n");
                                break loggedInMenu;
                        }
                    }
                } else {
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