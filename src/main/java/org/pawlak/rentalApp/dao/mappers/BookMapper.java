package org.pawlak.rentalApp.dao.mappers;

import org.pawlak.rentalApp.model.Book;
import org.pawlak.rentalApp.model.enums.BookGenres;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements RowMapper<Book> {
    @Override
    public Book map(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("description"),
                rs.getInt("releaseYear"),
                rs.getInt("pageCount"),
                BookGenres.valueOf(rs.getString("genre")),
                rs.getInt("countOfRates"),
                rs.getInt("sumOfRates"),
                rs.getBoolean("available")
        );
    }
}