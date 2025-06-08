package org.pawlak.rentalApp.service;

import org.mindrot.jbcrypt.BCrypt;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;
import org.pawlak.rentalApp.util.RegisterValidation;

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

        // Użycie walidatorów
        errors.addAll(RegisterValidation.validateEmail(email, isEmailTaken(email)));
        errors.addAll(RegisterValidation.validatePassword(password));
        BookGenres favoriteGenre = RegisterValidation.validateGenre(favGenreStr, errors);

        // Wyświetlenie błędów
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            return;
        } else {
            // Rejestracja użytkownika
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(0, name, email, hashedPassword, favoriteGenre, UserRole.USER);
            userDao.insert(newUser);
            System.out.println("Rejestracja zakończona sukcesem. Możesz się teraz zalogować.");
        }
    }

    public Optional<User> login (String email, String password) {
        return userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> BCrypt.checkpw(password, u.getPassword()))
                .findFirst();
    }

    public boolean validateAndUpdatePassword(User user, String newPassword) {
        List<String> errors = RegisterValidation.validatePassword(newPassword);
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            return false;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userDao.update(user);
        System.out.println("Hasło zostało zmienione pomyślnie.");
        return true;
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
