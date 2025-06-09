package org.pawlak.rentalApp.database;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;

import static org.mockito.Mockito.*;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectDBTest {

    @Test
    void shouldConnectAndCloseConnection() throws Exception {
        ConnectDB connectDB = new ConnectDB();

        Connection conn = connectDB.getConnection();
        assertThat(conn).isNotNull();
        assertThat(conn.isClosed()).isFalse();

        connectDB.closeConnection();
        assertThat(conn.isClosed()).isTrue();
    }

    @Test
    void shouldHandleCloseWhenConnectionIsNull() throws Exception {
        ConnectDB connectDB = new ConnectDB();

        // Ustawiamy pole 'connection' na null
        Field field = ConnectDB.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(connectDB, null);

        // Nie powinno rzucać wyjątku
        connectDB.closeConnection();
    }

    @Test
    void shouldHandleSQLExceptionWhenConnectionFails() {
        ConnectDB connectDB = new ConnectDB("jdbc:sqlite::invalid:");
        assertThat(connectDB.getConnection()).isNull();
    }

    @Test
    void shouldHandleSQLExceptionWhenConnectionPass() {
        ConnectDB connectDB = new ConnectDB("jdbc:sqlite:database.db");
        assertThat(connectDB.getConnection()).isNotNull();
    }

    @Test
    void shouldHandleSQLExceptionWhenClosingConnection() throws Exception {
        // mockujemy Connection
        Connection mockConnection = mock(Connection.class);

        // ustawiamy, że close() rzuca SQLException
        doThrow(new SQLException("Closing failed")).when(mockConnection).close();

        // tworzymy ConnectDB, ale nadpisujemy pole connection przez refleksję
        ConnectDB connectDB = new ConnectDB();

        // ustawiamy connection na mocka
        java.lang.reflect.Field field = ConnectDB.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(connectDB, mockConnection);

        // wywołujemy closeConnection, metoda powinna złapać wyjątek i go nie rzucać dalej
        connectDB.closeConnection();

        // test przejdzie, jeśli nie ma wyjątku
    }

}
