package org.pawlak.rentalApp.dao.mappers;

import org.pawlak.rentalApp.dao.BookDao;
import org.pawlak.rentalApp.dao.UserDao;
import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.Rental;
import org.pawlak.rentalApp.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RentalMapper implements RowMapper<Rental> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final BookDao bookDao;
    private final UserDao userDao;

    public RentalMapper(BookDao bookDao, UserDao userDao) {
        this.bookDao = bookDao;
        this.userDao = userDao;
    }

    @Override
    public Rental map(ResultSet rs) throws SQLException {
        int rentalId = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int bookId = rs.getInt("book_id");
        String rentalDateStr = rs.getString("rental_date");
        LocalDate rentalDate = rentalDateStr != null ? LocalDate.parse(rentalDateStr, FORMATTER) : null;

        String dueDateStr = rs.getString("due_date");
        LocalDate dueDate = dueDateStr != null ? LocalDate.parse(dueDateStr, FORMATTER) : null;

        String returnDateStr = rs.getString("return_date");
        LocalDate returnDate = returnDateStr != null ? LocalDate.parse(returnDateStr, FORMATTER) : null;

        Book book = bookDao.findById(bookId);

        User user = userDao.findById(userId);

        return new Rental(rentalId, user, book, rentalDate, dueDate, returnDate, 0);
    }
}
