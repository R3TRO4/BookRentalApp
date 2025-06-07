package org.pawlak.rentalApp.dao.mappers;

import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RentalMapper implements RowMapper<Rental> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public Rental map(ResultSet rs) throws SQLException {
        int rentalId = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int bookId = rs.getInt("book_id");
        LocalDate rentalDate = LocalDate.parse(rs.getString("rental_date"), FORMATTER);
        String returnDateStr = rs.getString("return_date");
        LocalDate returnDate = returnDateStr != null ? LocalDate.parse(returnDateStr, FORMATTER) : null;

        // Utwórz obiekty z samym ID – można je potem rozszerzyć przez DAO jeśli potrzeba
        User user = new User(userId, null, null, null, null);
        Book book = new Book(bookId, null, null, null, 0, 0, null, true);

        return new Rental(rentalId, user, book, rentalDate, returnDate);
    }
}
