package org.pawlak.rentalApp.model;

import java.time.LocalDate;

public class Rental {
    private int id;
    private User userId;
    private Book bookId;
    private LocalDate rentalDate;
    private LocalDate returnDate;

    public Rental(int id, User userId, Book bookId, LocalDate rentalDate, LocalDate returnDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public User getUserId() {
        return userId;
    }

    public Book getBookId() {
        return bookId;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }
}

