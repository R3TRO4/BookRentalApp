package org.pawlak.rentalApp.model;

import org.pawlak.rentalApp.model.enums.BookGenres;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private int releaseYear;
    private int pageCount;
    private BookGenres genre;
    private int countOfRates;
    private int sumOfRates;
    private boolean available;

    public Book(int id, String title, String author, String description, int releaseYear, int pageCount, BookGenres genre, int countOfRates, int sumOfRates, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.releaseYear = releaseYear;
        this.pageCount = pageCount;
        this.genre = genre;
        this.countOfRates = countOfRates;
        this.sumOfRates = sumOfRates;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int newReleaseYear) {
        releaseYear = newReleaseYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int newPageCount) {
        pageCount = newPageCount;
    }

    public BookGenres getGenre() {
        return genre;
    }

    public void setGenre(BookGenres newGenre) {
        genre = newGenre;
    }

    public int getSumOfRates() {
        return sumOfRates;
    }

    public void setSumOfRates(int sumOfRates) {
        this.sumOfRates = sumOfRates;
    }

    public int getCountOfRates() {
        return countOfRates;
    }

    public void setCountOfRates(int countOfRates) {
        this.countOfRates = countOfRates;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRating() {
        return countOfRates == 0 ? 0 : (double) sumOfRates / countOfRates;
    }
}
