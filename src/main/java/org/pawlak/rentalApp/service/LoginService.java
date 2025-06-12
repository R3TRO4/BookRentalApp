package org.pawlak.rentalApp.service;

import org.mindrot.jbcrypt.BCrypt;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.User;

import java.util.Optional;

public class LoginService {

    private final UserDao userDao;

    public LoginService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> login (String email, String password) {
        return userDao.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> BCrypt.checkpw(password, u.getPassword()))
                .findFirst();
    }
}
