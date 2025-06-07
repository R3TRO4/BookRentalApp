package org.pawlak.rentalApp.dao.mappers;

import org.pawlak.rentalApp.model.User;
import org.pawlak.rentalApp.model.enums.BookGenres;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("login"),
                rs.getString("password"),
                BookGenres.valueOf(rs.getString("favorite_genre"))
        );
    }
}
