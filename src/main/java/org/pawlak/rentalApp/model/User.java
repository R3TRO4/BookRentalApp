package org.pawlak.rentalApp.model;

import org.pawlak.rentalApp.model.enums.BookGenres;

public class User {
    private int id;
    private String firstName;
    private String email;
    private String password;
    private BookGenres favoriteGenre;

    public User(final int id, final String firstName, final String email, final String password, final BookGenres favoriteGenre) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.favoriteGenre = favoriteGenre;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public BookGenres getFavoriteGenre() {
        return favoriteGenre;
    }

    @Override
    public String toString() {
        return firstName + " (ulubiony gatunek: " + favoriteGenre + ")";
    }


}
