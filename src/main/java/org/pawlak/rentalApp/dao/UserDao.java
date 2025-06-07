package org.pawlak.rentalApp.dao;

import org.pawlak.rentalApp.dao.mappers.UserMapper;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao extends GenericDaoImpl<User> {
    public UserDao(Connection connection) {
        super(connection, new UserMapper(), "users");
    }

    @Override
    public void insert(User user) {
        String sql = "INSERT INTO users (firstName, email, password, favoriteGenre) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFavoriteGenre().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET firstName=?, email=?, password=?, favoriteGenre=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFavoriteGenre().name());
            stmt.setInt(5, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
