package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;

public class RatingService {
    private final BookDao bookDao;

    public RatingService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public void addRating(int bookId, int rating) {
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Ocena tylko w zakresie od 1 do 10.");
        }

        Book book = bookDao.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Nie znaleziono książki.");
        }

        book.setSumOfRates(book.getSumOfRates() + rating);
        book.setCountOfRates(book.getCountOfRates() + 1);
        bookDao.update(book);
    }
}
