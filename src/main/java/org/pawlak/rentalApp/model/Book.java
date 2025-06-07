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
    private boolean available;

    public Book(int id, String title, String author, String description, int releaseYear, int pageCount, BookGenres genre, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.releaseYear = releaseYear;
        this.pageCount = pageCount;
        this.genre = genre;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public BookGenres getGenre() {
        return genre;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
