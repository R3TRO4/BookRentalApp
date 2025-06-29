package org.pawlak.rentalApp.model;

import java.time.LocalDate;

public class Rental {
    private int id;
    private User user;
    private Book book;
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double penaltyFee;

    public Rental(int id, User user, Book book, LocalDate rentalDate, LocalDate dueDate, LocalDate returnDate, double penaltyFee) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.rentalDate = rentalDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.penaltyFee = penaltyFee;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void setReturnDate(LocalDate now) {
        returnDate = now;
    }

    public double getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(double penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public void setUser(User insertedUser) {
        user = insertedUser;
    }

    public void setBook(Book insertedBook) {
        book = insertedBook;
    }
}

