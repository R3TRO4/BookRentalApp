package org.pawlak.rentalApp;

import org.pawlak.rentalApp.database.ConnectDB;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;
import org.pawlak.rentalApp.service.*;
import org.pawlak.rentalApp.service.notifier.ConsoleNotifier;
import org.pawlak.rentalApp.service.notifier.Notifier;
import org.pawlak.rentalApp.util.AppContext;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        ConnectDB db = new ConnectDB();
        Connection connection = db.getConnection();

        if (connection != null) {
            AppContext context = new AppContext(connection);
            context.penaltyScheduler.start();
            mainMenu(context, db);
        } else {
            System.out.println("Connection error");
            System.exit(1);
        }
    }

    private static void handleRegistration (Scanner scanner, AppContext context){
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
            context.registerService.register(name, email, password, genre);
        } catch (IllegalArgumentException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private static Optional<User> handleLogin (Scanner scanner, AppContext context){
        while (true) {
            System.out.println("Email: ");
            String email = scanner.nextLine();
            System.out.println("Hasło: ");
            String password = scanner.nextLine();

            Optional<User> loggedUser = context.loginService.login(email, password);
            if (loggedUser.isPresent()) {
                User user = loggedUser.get();

                // powiadomienia
                Notifier notifier = new ConsoleNotifier();
                NotificationService notificationService = new NotificationService(context.rentalService, notifier);
                notificationService.checkAndNotify(user);

                // pierwsze logowanie admina
                if (user.getRole() == UserRole.ADMIN && user.getPassword().equals("$2a$10$YSaKnrEwbx0kXyp5qP5bQuD/BFIfb9nsSfA4iDyuCiSdaXim3B2RS")) {
                    System.out.println("Pierwsze logowanie jako administrator. Ustaw nowe hasło:");
                    String newPassword = scanner.nextLine();

                    if (!context.registerService.validateAndUpdatePassword(user, newPassword)) {
                        System.out.println("Spróbuj ponownie.");
                        return Optional.empty();
                    }
                }

                System.out.println("Zalogowano jako: " + user.getName());
                return Optional.of(user);
            } else {
                System.out.println("Błędny email lub hasło.");
            }
        }
    }

    private static void mainMenu (AppContext context, ConnectDB db){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Rejestracja\n2. Logowanie\n3. Zakończ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> handleRegistration(scanner, context);
                case 2 -> {
                    Optional<User> loggedUser = handleLogin(scanner, context);
                    if (loggedUser.isPresent() && loggedUser.get().getRole().equals(UserRole.USER)) {
                        // Przekaż dalej do menu użytkownika/admina
                        userMenu(loggedUser.get(), scanner, context);
                    } else {
                        adminMenu(loggedUser.get(), scanner, context);
                    }
                }
                case 3 -> {
                    db.closeConnection();
                    System.out.println("Do widzenia!");
                    System.exit(0);
                }
                default -> System.out.println("Nieprawidłowy wybór.\n\n");
            }
        }
    }


    //**************************************************************//
    //**************************USER AREA**************************//
    //**************************************************************//
    private static void getAvailableBooks(AppContext context) {
        List<Book> availableBooks = context.bookService.getAvailableBooks();
        availableBooks.forEach(book ->
                System.out.println(
                        book.getId() + ": " +
                                book.getTitle() +
                                "\nAutor: " + book.getAuthor() +
                                ",\nOpis: " + book.getDescription() +
                                "\nGatunek: " + book.getGenre() +
                                "\nŚrednia ocen: " + book.getRating() + "\n\n"));
    }

    private static void getAllBooks(AppContext context) {
        List<Book> allBooks = context.bookService.getAllBooks();
        allBooks.forEach(book ->
                System.out.println(
                        book.getId() + ": " +
                                book.getTitle() +
                                "\nAutor: " + book.getAuthor() +
                                ",\nOpis: " + book.getDescription() +
                                "\nGatunek: " + book.getGenre() +
                                "\nŚrednia ocen: " + book.getRating() +
                                "\nDostępność: " + book.isAvailable() + "\n\n"));
    }

    private static void getAllAvailableBooksByUserFavoriteGenre(User loggedUser, AppContext context) {
        List<Book> genreBooks = context.recommendationService.getAvailableBooksByUserFavoriteGenre(loggedUser);
        if (genreBooks.isEmpty()) {
            System.out.println("Brak dostępnych książek w twoim ulubionym gatunku.");
        } else {
            System.out.println("Dostępne książki w twoim ulubionym gatunku:");
            genreBooks.forEach(b -> System.out.println(
                    b.getTitle() + " - " + b.getAuthor() +
                            "\n" + b.getDescription() + "\n\n"));
        }
    }

    private static void top10Books(AppContext context) {
        List<Book> topBooks = context.statisticsService.getTop10MostPopularBooks();
        if (topBooks.isEmpty()) {
            System.out.println("Brak danych.");
        } else {
            for (int i = 0; i < topBooks.size(); i++) {
                Book book = topBooks.get(i);
                System.out.printf("%d. %s - wypożyczona %d razy\n",
                        i + 1,
                        book.getTitle(),
                        book.getTimesRented());
            }
        }
    }

    private static void rentABook(User loggedUser, Scanner scanner, AppContext context) {
        System.out.println("Podaj ID książki do wypożyczenia");
        int bookId = Integer.parseInt(scanner.nextLine());
        Optional<Book> bookOpt = context.bookService.getBookById(bookId);
        if (bookOpt.isPresent() && bookOpt.get().isAvailable()) {
            context.rentalService.rentBook(loggedUser, bookOpt.get());
            System.out.println("Wypożyczono książkę: " + bookOpt.get().getTitle());
        } else {
            System.out.println("Książka jest już wypożyczona.");
        }
    }

    private static void userRents(User loggedUser, AppContext context) {
        List<Rental> rentals = context.rentalService.getActiveRentalsForUser(loggedUser);
        if (rentals.isEmpty()) {
            System.out.println("Nie masz żadnych aktywnych wypożyczeń.");
        } else {
            rentals.forEach(r -> System.out.println("Książka: " + r.getBook().getTitle() +
                    ", wypożyczona: " + r.getRentalDate() + ", Do oddania przed: " + r.getDueDate()));
        }
    }

    private static void returnABook(User loggedUser, Scanner scanner, AppContext context) {
        List<Rental> activeRentals = context.rentalService.getActiveRentalsForUser(loggedUser);
        if (activeRentals.isEmpty()) {
            System.out.println("Nie masz żadnych aktywnych wypożyczeń.");
            return;
        }

        System.out.println("Twoje aktywne wypożyczenia:");
        for (Rental r : activeRentals) {
            System.out.println("ID wypożyczenia: " + r.getId() + ", Książka " + r.getBook().getTitle());
        }

        System.out.println("Podaj ID wypożyczenia do zwrotu");
        int rentalId = Integer.parseInt(scanner.nextLine());

        try {
            context.rentalService.returnBook(rentalId);
            System.out.println("Ksiązka została zwrócona.\n");
            rateABook(scanner, context, rentalId);
        } catch (Exception e) {
            System.out.println("Błąd przy zwrocie: " + e.getMessage());
        }
    }

    private static void rateABook(Scanner scanner, AppContext context, int rentalId) {
        Optional<Rental> bookToRate = context.rentalService.getRentalById(rentalId);

        if (bookToRate.isPresent()) {
            System.out.println("Jak oceniasz tą książkę w skali od 1 do 10?\n" + "Twoja ocena:");
            int bookToRateId = bookToRate.get().getBook().getId();
            int rate = Integer.parseInt(scanner.nextLine());
            context.ratingService.addRating(bookToRateId, rate);
            System.out.println("Dziękujemy za ocenę!");
        }
    }

    private static void rentHistory(User loggedUser, AppContext context) {
        List<Rental> history = context.rentalService.getRentalHistoryForUser(loggedUser);
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
    }

    public static void userMenu(User loggedUser, Scanner scanner, AppContext context) {
        loggedInMenu:
        while (true) {
            System.out.println("\nWybierz opcję:");
            System.out.println("1. Wyświetl dostępne książki");
            System.out.println("2. Wyświetl wszystkie książki");
            System.out.println("3. Wyświetl dostępne książki na podstawie ulubionego gatunku");
            System.out.println("4. Wyświetl 10 najpopularniejszych książek");
            System.out.println("5. Wypożycz książkę");
            System.out.println("6. Moje wypożyczenia");
            System.out.println("7. Zwróć książkę");
            System.out.println("8. Historia wypożyczeń");
            System.out.println("9. Wyloguj");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    getAvailableBooks(context);
                    break;
                case "2":
                    getAllBooks(context);
                    break;
                case "3":
                    getAllAvailableBooksByUserFavoriteGenre(loggedUser, context);
                    break;
                case "4":
                    top10Books(context);
                    break;
                case "5":
                    rentABook(loggedUser, scanner, context);
                    break;
                case "6":
                    userRents(loggedUser, context);
                    break;
                case "7":
                    returnABook(loggedUser, scanner, context);
                    break;
                case "8":
                    rentHistory(loggedUser, context);
                    break;
                case "9":
                    System.out.println("Wylogowano.\n\n");
                    break loggedInMenu;
                default:
                    System.out.println("Nieprawidłowa opcja.\n\n");
            }
        }
    }

    //**************************************************************//
    //**************************ADMIN AREA**************************//
    //**************************************************************//
    private static void adminMenu(User user, Scanner scanner, AppContext context) {
        loggedInMenu: while (true) {
            System.out.println("ADMIN PANEL:");
            System.out.println("1. Dodaj książkę");
            System.out.println("2. Edytuj książkę");
            System.out.println("3. Usuń książkę");
            System.out.println("4. Wyświetl wszystkich użytkowników");
            System.out.println("5. Wyświetl wszystkie wypożyczenia");
            System.out.println("6. Wyświetl wszystkie aktywne wypożyczenia");
            System.out.println("7. Wyświetl wszystkie statystyki");
            System.out.println("8. Wyloguj");

            String adminChoice = scanner.nextLine();

            switch (adminChoice) {
                case "1":
                    addNewBook(scanner, context);
                    break;
                case "2":
                    editBook(scanner, context);
                    break;
                case "3":
                    deleteBook(scanner, context);
                    break;
                case "4":
                    showAllUsers(context);
                case "5":
                    showAllRentals(context);
                case "6":
                    showAllActiveRentals(context);
                case "7":
                    showAllStatistics(context);
                case "8":
                    System.out.println("Wylogowano.\n\n");
                    break loggedInMenu;
                default:
                    System.out.println("Nieprawidłowa opcja.\n\n");
            }
        }
    }

    private static void addNewBook(Scanner scanner, AppContext context) {
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
            Book book = new Book(0, title, author, description, releaseYear, pageCount, genre, 0, 0, true, 0);
            context.bookService.addBook(book);
            System.out.println("Dodano książkę.");
        } catch (IllegalArgumentException e) {
            System.out.println("Niepoprawny gatunek.");
        }
    }

    private static void editBook(Scanner scanner, AppContext context) {
        System.out.println("Podaj ID książki do edycji:");
        int editId = Integer.parseInt(scanner.nextLine());
        Optional<Book> bookOpt = context.bookService.getBookById(editId);
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
                try {
                    int newReleaseYearInt = Integer.parseInt(newReleaseYear);
                    b.setReleaseYear(newReleaseYearInt);
                } catch (NumberFormatException e) {
                    System.out.println("Nieprawidłowa liczba. Zmiana została pominięta");
                }
            }

            System.out.println("Nowy rok wydania: ");
            String newPageCount = scanner.nextLine();
            if (!newPageCount.isBlank()) {
                try {
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
            if (!newGenreStr.isBlank()) {
                try {
                    BookGenres newGenre = BookGenres.valueOf(newGenreStr.toUpperCase());
                    b.setGenre(newGenre);
                } catch (IllegalArgumentException e) {
                    System.out.println(("Invalid book genre: " + e.getMessage()));
                }
            }

            context.bookService.updateBook(b);
            System.out.println("Zaktualizowano książkę.");
        } else {
            System.out.println("Nie znaleziono książki.");
        }
    }

    private static void deleteBook(Scanner scanner, AppContext context) {
        System.out.println("Podaj ID książki do usunięcia:");
        int deleteId = Integer.parseInt(scanner.nextLine());
        context.bookService.deleteBook(deleteId);
        System.out.println("Usunięto książkę.");
    }

    private static void showAllUsers(AppContext context) {
        List<User> allUsers = context.userService.getAllUsers();
        for (User u : allUsers) {
            int activeCount = context.rentalService.getActiveRentalsForUser(u).size();
            System.out.println(u.getId() + ": " + u.getName() + " - " + u.getEmail() + " | Aktywne wypożyczenia: " + activeCount);
        }
    }

    private static void showAllRentals(AppContext context) {
        List<Rental> allRentals = context.rentalService.getAllRentals();
        allRentals.forEach(r ->
                System.out.println(
                        r.getId() + ": " +
                                "bookId: " + r.getBook() +
                                ", BorrowerId: " + r.getUser() +
                                ", Rental date: " + r.getRentalDate() +
                                ", Due date: " + r.getDueDate() +
                                ", Returned: " + r.isReturned()));
    }

    private static void showAllActiveRentals(AppContext context) {
        List<Rental> allActiveRentals = context.rentalService.getAllRentals();
        allActiveRentals.forEach(r ->
                System.out.println(
                        r.getId() + ": " +
                                "bookId: " + r.getBook() +
                                ", BorrowerId: " + r.getUser() +
                                ", Rental date: " + r.getRentalDate() +
                                ", Due date: " + r.getDueDate()));
    }

    private static void showAllStatistics(AppContext context) {
        System.out.println("\n=== Statystyki ===");

        System.out.println("📚 Liczba wszystkich wypożyczeń: " + context.statisticsService.getTotalRentals());

        System.out.println("\n🏆 Top 10 najczęściej wypożyczanych książek:");
        top10Books(context);

        System.out.println("\n👥 Top 5 najbardziej aktywnych użytkowników:");
        top5Users(context);
    }

    private static void top5Users(AppContext context) {
        List<User> topUsers = context.statisticsService.getTop5MostActiveUsers();
        if (topUsers.isEmpty()) {
            System.out.println("Brak danych.");
        } else {
            for (int i = 0; i < topUsers.size(); i++) {
                User user = topUsers.get(i);
                System.out.printf("%d. %s (%s)\n", i + 1, user.getName(), user.getEmail());
            }
        }
    }
}


