package org.pawlak.rentalApp.model;

import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private BookGenres favoriteGenre;
    private UserRole role;

    public User(final int id, final String name, final String email, final String password, final BookGenres favoriteGenre, final UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.favoriteGenre = favoriteGenre;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String annaNowak) { this.name = annaNowak; }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public BookGenres getFavoriteGenre() {
        return favoriteGenre;
    }

    public UserRole getRole() {
        return role;
    }

    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }


}
