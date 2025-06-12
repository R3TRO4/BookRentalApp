package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RatingServiceTest {

    private BookDao bookDao;
    private RatingService ratingService;

    @BeforeEach
    public void setUp() {
        bookDao = mock(BookDao.class);
        ratingService = new RatingService(bookDao);
    }

    @Test
    public void shouldAddRatingCorrectly() {
        // given
        Book book = new Book(1, "Tytuł", "Autor", "Opis", 2020, 300, null, 0, 0, true);
        when(bookDao.findById(1)).thenReturn(book);

        // when
        ratingService.addRating(1, 8);

        // then
        assertEquals(8, book.getSumOfRates());
        assertEquals(1, book.getCountOfRates());
        verify(bookDao).update(book);
    }

    @Test
    public void shouldAddTwoRatingsCorrectly() {
        // given
        Book book = new Book(1, "Tytuł", "Autor", "Opis", 2020, 300, null, 0, 0, true);
        when(bookDao.findById(1)).thenReturn(book);

        // when
        ratingService.addRating(1, 8);

        assertEquals(8, book.getSumOfRates());
        assertEquals(1, book.getCountOfRates());
        verify(bookDao).update(book);

        ratingService.addRating(1, 6);

        // then
        assertEquals(14, book.getSumOfRates());
        assertEquals(2, book.getCountOfRates());
        verify(bookDao, times(2)).update(book);
    }


    @Test
    public void shouldThrowExceptionForInvalidRating() {
        // rating out of range
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.addRating(1, 11);
        });
        assertEquals("Ocena tylko w zakresie od 1 do 10.", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenBookNotFound() {
        // given
        when(bookDao.findById(999)).thenReturn(null);

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ratingService.addRating(999, 5);
        });
        assertEquals("Nie znaleziono książki.", exception.getMessage());
    }
}
