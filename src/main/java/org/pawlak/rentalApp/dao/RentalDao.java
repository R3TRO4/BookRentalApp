package org.pawlak.rentalApp.dao;

import org.pawlak.rentalApp.dao.mappers.RentalMapper;
import org.pawlak.rentalApp.model.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class RentalDao extends GenericDaoImpl<Rental> {

    public RentalDao(Connection connection, BookDao bookDao, UserDao userDao) {
        super(connection, new RentalMapper(bookDao, userDao), "rentals");
    }

    @Override
    public void insert(Rental rental) {
        String sql = "INSERT INTO rentals(user_id, book_id, rental_date, due_date, return_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rental.getUser().getId());
            stmt.setInt(2, rental.getBook().getId());
            stmt.setString(3, rental.getRentalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            stmt.setString(4, rental.getDueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            if (rental.getReturnDate() != null) {
                stmt.setString(5, rental.getReturnDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Rental rental) {
        String sql = "UPDATE rentals SET user_id = ?, book_id = ?, rental_date = ?, due_date = ?, return_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rental.getUser().getId());
            stmt.setInt(2, rental.getBook().getId());
            stmt.setString(3, rental.getRentalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            stmt.setString(4, rental.getDueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            stmt.setString(5, rental.getReturnDate() != null
                    ? rental.getReturnDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    : null);
            stmt.setInt(6, rental.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
