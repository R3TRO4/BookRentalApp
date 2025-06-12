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

//    public void updateUser(User user) {
//        userDao.update(user);
//    }

//    public void addUser(User newUser) {
//        userDao.insert(newUser);
//    }

//    public void deleteUser(int id) {
//        userDao.delete(id);
//    }
}
