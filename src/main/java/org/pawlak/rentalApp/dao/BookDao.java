package org.pawlak.rentalApp.dao;

import org.pawlak.rentalApp.dao.mappers.BookMapper;
import org.pawlak.rentalApp.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDao extends GenericDaoImpl<Book> {

    public BookDao(Connection connection) {
        super(connection, new BookMapper(), "books");
    }

    @Override
    public void insert(Book book) {
        String sql = "INSERT INTO books(title, author, description, releaseYear, pageCount, genre, countOfRates, sumOfRates, available, timesRented) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getDescription());
            stmt.setInt(4, book.getReleaseYear());
            stmt.setInt(5, book.getPageCount());
            stmt.setString(6, book.getGenre().name());
            stmt.setInt(7, book.getCountOfRates());
            stmt.setInt(8, book.getSumOfRates());
            stmt.setBoolean(9, book.isAvailable());
            stmt.setLong(10, book.getTimesRented());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert book", e);
        }
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE books SET title=?, author=?, description=?, releaseYear=?, pageCount=?, genre=?, countOfRates=?, sumOfRates=?, available=?, timesRented=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getDescription());
            stmt.setInt(4, book.getReleaseYear());
            stmt.setInt(5, book.getPageCount());
            stmt.setString(6, book.getGenre().name());
            stmt.setInt(7, book.getCountOfRates());
            stmt.setInt(8, book.getSumOfRates());
            stmt.setBoolean(9, book.isAvailable());
            stmt.setLong(10, book.getTimesRented());
            stmt.setInt(11, book.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }

    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get available books", e);
        }
        return books;
    }

    public List<Book> findAvailableBooksByGenre(String genre) {
        String sql = "SELECT * FROM books WHERE genre = ? AND available = 1";
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, genre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available books by genre", e);
        }
        return books;
    }
}
