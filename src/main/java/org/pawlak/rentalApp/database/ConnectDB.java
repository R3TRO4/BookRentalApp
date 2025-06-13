package org.pawlak.rentalApp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private Connection connection;

    public ConnectDB() {
        String url = "jdbc:sqlite:database.db";
        try{
            connection = DriverManager.getConnection(url);
            System.out.println("Database: Connection Successful");
        } catch (SQLException e) {
            System.out.println("Database: Connection Failed");
            e.printStackTrace();
        }
    }

    //#####TESTONLY#####//
    public ConnectDB(String url) {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Database: Connection Successful");
        } catch (SQLException e) {
            System.out.println("Database: Connection Failed");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try{
            if (connection != null) {
                System.out.println("Database: Connection closed");
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
