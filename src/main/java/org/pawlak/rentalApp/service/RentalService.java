package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.RentalDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalService {

    private final RentalDao rentalDao;
    private final BookDao bookDao;

    public RentalService(RentalDao rentalDao, BookDao bookDao) {
        this.rentalDao = rentalDao;
        this.bookDao = bookDao;
    }

    public List<Rental> getAllRentals() {
        return rentalDao.findAll();
    }

    public List<Rental> getActiveRentals() {
        return rentalDao.findAll().stream()
                .filter(r -> !r.isReturned())
                .collect(Collectors.toList());
    }

    public void rentBook(User user, Book book) {
        if (!book.isAvailable()) {
            throw new IllegalStateException("Książka jest już wypożyczona.");
        }

        Rental rental = new Rental(0, user, book, LocalDate.now(), null);
        rentalDao.insert(rental);

        // Ustaw książkę jako niedostępną
        book.setAvailable(false);
        bookDao.update(book);
    }

    public void returnBook(int rentalId) {
        Rental rental = rentalDao.findById(rentalId);
        if (rental == null || rental.isReturned()) {
            throw new IllegalStateException("Nie można zwrócić tej książki.");
        }

        // Zaktualizuj datę zwrotu
        Rental updatedRental = new Rental(
                rental.getId(),
                rental.getUserId(),
                rental.getBookId(),
                rental.getRentalDate(),
                LocalDate.now()
        );
        rentalDao.update(updatedRental);

        // Ustaw książkę jako dostępną
        Book book = rental.getBookId();
        book.setAvailable(true);
        bookDao.update(book);
    }

    public Optional<Rental> getRentalById(int id) {
        return Optional.ofNullable(rentalDao.findById(id));
    }
}
