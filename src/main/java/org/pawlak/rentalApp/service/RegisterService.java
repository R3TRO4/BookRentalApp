package org.pawlak.rentalApp.service;

import org.mindrot.jbcrypt.BCrypt;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;
import org.pawlak.rentalApp.model.enums.UserRole;
import org.pawlak.rentalApp.util.RegisterValidation;

import java.util.ArrayList;
import java.util.List;

public class RegisterService {

    private final UserDao userDao;

    public RegisterService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isEmailTaken(String email) {
        return userDao.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public void register(String name, String email, String password, String favGenreStr) {
        List<String> errors = new ArrayList<>();

        errors.addAll(RegisterValidation.validateEmail(email, isEmailTaken(email)));
        errors.addAll(RegisterValidation.validatePassword(password));
        BookGenres favoriteGenre = RegisterValidation.validateGenre(favGenreStr, errors);

        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
        } else {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(0, name, email, hashedPassword, favoriteGenre, UserRole.USER);
            userDao.insert(newUser);
            System.out.println("Rejestracja zakończona sukcesem. Możesz się teraz zalogować.");
        }
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
}
