package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticsServiceTest {

    private RentalService rentalService;
    private StatisticsService statisticsService;

    private User user1 = new User(1, "Bartosz", "bartosz@example.com", "Password1", BookGenres.FANTASY, UserRole.USER);
    private User user2 = new User(2, "Paula", "paula@example.com", "Password2", BookGenres.ROMANCE, UserRole.USER);
    private Book book1 = new Book(1, "Book A", "Author", "Desc", 2020, 200, null, 0, 0, true,0);
    private Book book2 = new Book(2, "Book B", "Author", "Desc", 2021, 250, null, 0, 0, true, 0);

    @BeforeEach
    void setup() {
        rentalService = mock(RentalService.class);
        statisticsService = new StatisticsService(rentalService);
    }

    @Test
    void TC_073_shouldReturnTotalRentals() {
        when(rentalService.getAllRentals()).thenReturn(List.of(
                new Rental(1, user1, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(2, user2, book2, LocalDate.now(), LocalDate.now().plusDays(7), null, 0)
        ));

        long total = statisticsService.getTotalRentals();

        assertEquals(2, total);
    }

    @Test
    void TC_074_shouldReturnMostPopularBook() {
        when(rentalService.getAllRentals()).thenReturn(List.of(
                new Rental(1, user1, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(2, user2, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(3, user2, book2, LocalDate.now(), LocalDate.now().plusDays(7), null, 0)
        ));

        Book result = statisticsService.getMostPopularBook();

        assertEquals(book1, result);
    }

    @Test
    void TC_075_shouldReturnTop10MostPopularBooks() {
        when(rentalService.getAllRentals()).thenReturn(Arrays.asList(
                new Rental(1, user1, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(2, user2, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(3, user2, book2, LocalDate.now(), LocalDate.now().plusDays(7), null, 0)
        ));

        List<Book> topBooks = statisticsService.getTop10MostPopularBooks();

        assertEquals(2, topBooks.size());
        assertEquals(book1, topBooks.get(0)); // book1 has 2 rentals
    }

    @Test
    void TC_076_shouldReturnTop5MostActiveUsers() {
        when(rentalService.getAllRentals()).thenReturn(Arrays.asList(
                new Rental(1, user1, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(2, user1, book2, LocalDate.now(), LocalDate.now().plusDays(7), null, 0),
                new Rental(3, user2, book1, LocalDate.now(), LocalDate.now().plusDays(7), null, 0)
        ));

        List<User> topUsers = statisticsService.getTop5MostActiveUsers();

        assertEquals(2, topUsers.size());
        assertEquals(user1, topUsers.get(0)); // user1 has 2 rentals
    }
}
