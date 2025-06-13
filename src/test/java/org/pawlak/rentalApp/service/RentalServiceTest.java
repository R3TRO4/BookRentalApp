package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.RentalDao;
import org.pawlak.rentalApp.model.*;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RentalServiceTest {

    private RentalDao rentalDao;
    private BookDao bookDao;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalDao = mock(RentalDao.class);
        bookDao = mock(BookDao.class);
        rentalService = new RentalService(rentalDao, bookDao);
    }

    @Test
    void shouldReturnAllRentals() {
        List<Rental> rentals = List.of(mock(Rental.class), mock(Rental.class));
        when(rentalDao.findAll()).thenReturn(rentals);

        List<Rental> result = rentalService.getAllRentals();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnOnlyActiveRentals() {
        Rental returned = new Rental(1, null, null, LocalDate.now(), LocalDate.now().plusDays(14), LocalDate.now(), 0);
        Rental active = new Rental(2, null, null, LocalDate.now(), LocalDate.now().plusDays(14), null, 0);
        when(rentalDao.findAll()).thenReturn(List.of(returned, active));

        List<Rental> result = rentalService.getActiveRentals();

        assertThat(result).containsExactly(active);
    }

    @Test
    void shouldRentBookAndMarkItUnavailable() {
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(1, "Dune", "Herbert", "desc", 1965, 400, BookGenres.SCIENCE_FICTION,  0, 0,true, 0);

        rentalService.rentBook(user, book);

        assertThat(book.isAvailable()).isFalse();
        verify(rentalDao).insert(any(Rental.class));
        verify(bookDao).update(book);
    }

    @Test
    void shouldReturnBookAndMarkItAvailable() {
        Book book = new Book(1, "Dune", "Herbert", "desc", 1965, 400, BookGenres.SCIENCE_FICTION,  0, 0,false, 0);
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.SCIENCE_FICTION, UserRole.USER);
        Rental rental = new Rental(1, user, book, LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), null, 0);

        when(rentalDao.findById(1)).thenReturn(rental);

        rentalService.returnBook(1);

        assertThat(book.isAvailable()).isTrue();
        verify(rentalDao).update(any(Rental.class));
        verify(bookDao).update(book);
    }

    @Test
    void shouldThrowExceptionWhenReturningAlreadyReturnedBook() {
        Rental rental = new Rental(1, null, null, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now(), 0);
        when(rentalDao.findById(1)).thenReturn(rental);

        assertThatThrownBy(() -> rentalService.returnBook(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nie można zwrócić tej książki.");
    }

    @Test
    void shouldReturnEmptyOptionalWhenRentalNotFound() {
        when(rentalDao.findById(100)).thenReturn(null);

        Optional<Rental> result = rentalService.getRentalById(100);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOnlyActiveRentalsForUser() {
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        Rental activeRental = new Rental(1, user, null, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);
        Rental returnedRental = new Rental(2, user, null, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now(), 0);
        Rental otherUserRental = new Rental(3, new User(2, "Anna", "anna@test.com", "pass", BookGenres.SCIENCE_FICTION, UserRole.USER), null, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);

        when(rentalDao.findAll()).thenReturn(List.of(activeRental, returnedRental, otherUserRental));

        List<Rental> result = rentalService.getActiveRentalsForUser(user);

        assertThat(result).containsExactly(activeRental);
    }

    @Test
    void shouldReturnRentalHistoryForUser() {
        User user = new User(1, "Jan", "jan@test.com", "pass", BookGenres.FANTASY, UserRole.USER);
        Rental returnedRental = new Rental(1, user, null, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now(), 0);
        Rental activeRental = new Rental(2, user, null, LocalDate.now(), LocalDate.now().plusDays(10), null, 0);
        Rental otherUserReturned = new Rental(3, new User(2, "Anna", "anna@test.com", "pass", BookGenres.SCIENCE_FICTION, UserRole.USER), null, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now(), 0);

        when(rentalDao.findAll()).thenReturn(List.of(returnedRental, activeRental, otherUserReturned));

        List<Rental> result = rentalService.getRentalHistoryForUser(user);

        assertThat(result).containsExactly(returnedRental);
    }

    @Test
    void shouldThrowExceptionWhenRentalNotFound() {
        when(rentalDao.findById(999)).thenReturn(null);

        assertThatThrownBy(() -> rentalService.returnBook(999))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nie można zwrócić tej książki.");
    }

    @Test
    void shouldThrowExceptionWhenRentalAlreadyReturned() {
        User user = new User(1, "Test", "test@mail.com", "pass", BookGenres.FANTASY, UserRole.USER);
        Book book = new Book(1, "Title", "Author", "Desc", 2000, 123, BookGenres.FANTASY,  0, 0, false, 0);
        Rental rental = new Rental(1, user, book, LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), LocalDate.now(), 0);

        when(rentalDao.findById(1)).thenReturn(rental);

        assertThatThrownBy(() -> rentalService.returnBook(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nie można zwrócić tej książki.");
    }

    @Test
    public void shouldCallUpdateOnRentalDao() {
        // given
        Rental rental = mock(Rental.class);

        // when
        rentalService.updateRental(rental);

        // then
        verify(rentalDao).update(rental);
    }

}
