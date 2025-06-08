package org.pawlak.rentalApp.service;

import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public Optional<User> getUserById(int id) {
        User user = userDao.findById(id);
        return Optional.ofNullable(user);
    }

    public boolean isEmailTaken(String email) {
        return userDao.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public void register(String name, String email, String password, String favGenreStr) {
        List<String> errors = new ArrayList<>();

        // Walidacja e-maila
        if (!email.contains("@")) {
            errors.add("Email address is invalid");
        }
        if (isEmailTaken(email)){
            errors.add("An account for the given email already exists");
        }

        // Walidacja hasła
        if (password.length() < 8) {
            errors.add("Password must be at least 8 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one digit");
        }

        // Walidacja gatunku
        BookGenres favoriteGenre = null;
        try {
            favoriteGenre = BookGenres.valueOf(favGenreStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Invalid book genre: " + favGenreStr);
        }

        // Wyświetlenie błędów
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            return;
        } else {
            // Rejestracja użytkownika
            User newUser = new User(0, name, email, password, favoriteGenre);
            userDao.insert(newUser);
            System.out.println("Registration successful");
        }
    }

    public Optional<User> login (String email, String password) {
        return userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst();
    }

    public void updateUser(User user) {
        userDao.update(user);
    }

    public void addUser(User newUser) {
        userDao.insert(newUser);
    }

    public void deleteUser(int id) {
        userDao.delete(id);
    }
}
