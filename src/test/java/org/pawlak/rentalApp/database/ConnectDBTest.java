package org.pawlak.rentalApp.database;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;

import static org.mockito.Mockito.*;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectDBTest {

    @Test
    void TC_079_shouldConnectAndCloseConnection() throws Exception {
        ConnectDB connectDB = new ConnectDB();

        Connection conn = connectDB.getConnection();
        assertThat(conn).isNotNull();
        assertThat(conn.isClosed()).isFalse();

        connectDB.closeConnection();
        assertThat(conn.isClosed()).isTrue();
    }

    @Test
    void TC_080_shouldHandleCloseWhenConnectionIsNull() throws Exception {
        ConnectDB connectDB = new ConnectDB();

        Field field = ConnectDB.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(connectDB, null);

        connectDB.closeConnection();
    }

    @Test
    void TC_081_shouldHandleWhenConnectionPass() {
        // Prawidłowy link
        ConnectDB connectDB = new ConnectDB("jdbc:sqlite:database.db");
        assertThat(connectDB.getConnection()).isNotNull();
    }

    //**************************************************************//
    //***********************Exemptions testing*********************//
    //**************************************************************//

    @Test
    void TC_082_shouldHandleSQLExceptionWhenConnectionFails() {
        // Nieprawidłowy link
        ConnectDB connectDB = new ConnectDB("jdbc:sqlite::invalid:");
        assertThat(connectDB.getConnection()).isNull();
    }

    @Test
    void TC_083_shouldHandleSQLExceptionWhenClosingConnection() throws Exception {
        // Połączenie mockowane (symulowane)
        Connection mockConnection = mock(Connection.class);

        // Kiedy metoda close() zostanie wywołana na mocku, ma zostać rzucony wyjątek
        doThrow(new SQLException("Closing failed")).when(mockConnection).close();

        ConnectDB connectDB = new ConnectDB(); // Rzeczywiste połączenie z bazą

        java.lang.reflect.Field field = ConnectDB.class.getDeclaredField("connection"); // Odwołuje się do pola po nazwie
        field.setAccessible(true); // Omija modyfikator dostępu private
        field.set(connectDB, mockConnection); // Zamiana prawdziwego połączenia na mockowane połączenie

        // Zamknięcie połączenia
        connectDB.closeConnection();
    }
}
