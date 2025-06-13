package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;
import org.pawlak.rentalApp.service.notifier.ConsoleNotifier;
import org.pawlak.rentalApp.service.notifier.Notifier;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private RentalService rentalService;
    private NotificationService notificationService;
    private Notifier notifier;

    @BeforeEach
    public void setUp() {
        rentalService = mock(RentalService.class);
        notifier = mock(Notifier.class);
        notificationService = new NotificationService(rentalService, notifier);
    }

    @Test
    public void shouldNotifyUserWhenReturnDateIsComing() {
        // given
        User user = new User(1, "Bartosz", "bartosz.pawlak@example.com", "Password1", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(2, "Tytuł książki", "Autor", "Opis", 2001, 300, BookGenres.FANTASY, 0, 0, true,0);

        LocalDate today = LocalDate.now();
        LocalDate dueDate3 = today.plusDays(3);
        LocalDate dueDate2 = today.plusDays(2);
        LocalDate dueDate1 = today.plusDays(1);

        Rental rental3 = new Rental(1, user, book, today.minusDays(4), dueDate3, null, 0);
        Rental rental2 = new Rental(2, user, book, today.minusDays(3), dueDate2, null, 0);
        Rental rental1 = new Rental(3, user, book, today.minusDays(2), dueDate1, null, 0);

        when(rentalService.getActiveRentalsForUser(user)).thenReturn(List.of(rental3, rental2, rental1));

        NotificationService notificationService = new NotificationService(rentalService, notifier);

        notificationService.checkAndNotify(user);

        verify(notifier).notify(eq(user), contains("Zostały 3 dni do zwrotu książki: " + book.getTitle()));
        verify(notifier).notify(eq(user), contains("Zostały 2 dni do zwrotu książki: " + book.getTitle()));
        verify(notifier).notify(eq(user), contains("Został 1 dzień do zwrotu książki: " + book.getTitle()));

    }

    @Test
    public void shouldPrintNotificationToConsole() {
        // given
        User user = new User(1, "Bartosz", "bartosz.pawlak@example.com", "Password1", BookGenres.FANTASY, UserRole.USER);
        String message = "To jest testowa wiadomość";
        ConsoleNotifier consoleNotifier = new ConsoleNotifier();

        // przechwycenie System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // when
        consoleNotifier.notify(user, message);

        // then
        String expectedOutput = "Powiadomienie dla Bartosz: " + message + System.lineSeparator();
        assertEquals(expectedOutput, outContent.toString());

        // przywrócenie System.out
        System.setOut(originalOut);
    }


}
