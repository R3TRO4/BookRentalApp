package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class RecommendationService {
    private final BookDao bookDao;

    public RecommendationService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public List<Book> getAvailableBooksByUserFavoriteGenre(User user) {
        return bookDao.findAvailableBooksByGenre(user.getFavoriteGenre().name());
    }

    public List<Book> getAvailableBooksByRating() {
        return bookDao.findAll().stream()
                .filter(Book::isAvailable)
                .filter(book -> book.getCountOfRates() > 0)
                .sorted((b1, b2) -> Double.compare(
                        b2.getRating(),
                        b1.getRating()
                ))
                .limit(10)
                .collect(Collectors.toList());
    }
}
