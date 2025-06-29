package org.pawlak.rentalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.enums.BookGenres;

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
    public void TC_050_shouldAddRatingCorrectly() {
        // given
        Book book = new Book(1, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
        when(bookDao.findById(1)).thenReturn(book);

        // when
        ratingService.addRating(1, 8);

        // then
        assertEquals(8, book.getSumOfRates());
        assertEquals(1, book.getCountOfRates());
        verify(bookDao).update(book);
    }

    @Test
    public void TC_051_shouldAddTwoRatingsCorrectly() {
        // given
        Book book = new Book(1, "Wiedźmin: ostatnie życzenie", "Andrzej Sapkowski", "Pierwszy tom opowiadań o Wiedźminie Geralcie", 1993, 330, BookGenres.FANTASY,  0, 0,true, 0);
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

    //**************************************************************//
    //***********************Exemptions testing*********************//
    //**************************************************************//
    @Test
    public void TC_052_shouldThrowExceptionForInvalidRating() {
        // rating out of range
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ratingService.addRating(1, 11));
        assertEquals("Ocena tylko w zakresie od 1 do 10.", exception.getMessage());
    }

    @Test
    public void TC_053_shouldThrowExceptionWhenBookNotFound() {
        // given
        when(bookDao.findById(999)).thenReturn(null);

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ratingService.addRating(999, 5));
        assertEquals("Nie znaleziono książki.", exception.getMessage());
    }
}
