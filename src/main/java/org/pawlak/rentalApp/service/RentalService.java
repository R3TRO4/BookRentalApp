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

    public List<Rental> getActiveRentalsForUser(User user) {
        return rentalDao.findAll().stream()
                .filter(r -> r.getUser().getId() == user.getId() && !r.isReturned())
                .collect(Collectors.toList());
    }

    public List<Rental> getRentalHistoryForUser(User user) {
        return rentalDao.findAll().stream()
                .filter(r -> r.getUser().getId() == user.getId() && r.isReturned())
                .collect(Collectors.toList());
    }

    public void updateRental(Rental rental) {
        rentalDao.update(rental);
    }

    public Optional<Rental> getRentalById(int id) {
        return Optional.ofNullable(rentalDao.findById(id));
    }

    public void rentBook(User user, Book book) {
        Rental rental = new Rental(0, user, book, LocalDate.now(), LocalDate.now().plusMonths(1), null, 0);
        rentalDao.insert(rental);

        book.setAvailable(false);
        bookDao.update(book);
    }

    public void returnBook(int rentalId) {
        Rental rental = rentalDao.findById(rentalId);
        if (rental == null || rental.isReturned()) {
            throw new IllegalStateException("Nie można zwrócić tej książki.");
        }

        Rental updatedRental = new Rental(
                rental.getId(),
                rental.getUser(),
                rental.getBook(),
                rental.getRentalDate(),
                rental.getDueDate(),
                LocalDate.now(),
                rental.getPenaltyFee()
        );
        rentalDao.update(updatedRental);

        Book book = rental.getBook();
        book.setAvailable(true);
        book.setTimesRented(book.getTimesRented() + 1);
        bookDao.update(book);
    }
}
