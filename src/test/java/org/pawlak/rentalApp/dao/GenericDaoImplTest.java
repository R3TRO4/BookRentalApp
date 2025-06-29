package org.pawlak.rentalApp.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GenericDaoImplTest {

    private Connection connection;
    private TestEntityDao dao;

    static class TestEntity {
        int id;
        String name;

        TestEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // Prosty mapper do TestEntity
    static class TestEntityMapper implements org.pawlak.rentalApp.dao.mappers.RowMapper<TestEntity> {
        @Override
        public TestEntity map(ResultSet rs) throws SQLException {
            return new TestEntity(rs.getInt("id"), rs.getString("name"));
        }
    }

    // Testowa implementacja GenericDaoImpl
    static class TestEntityDao extends GenericDaoImpl<TestEntity> {
        public TestEntityDao(Connection connection) {
            super(connection, new TestEntityMapper(), "test_entities");
        }

        @Override
        public void insert(TestEntity entity) {
            String sql = "INSERT INTO test_entities(id, name) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, entity.id);
                stmt.setString(2, entity.name);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void update(TestEntity entity) {
            String sql = "UPDATE test_entities SET name = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, entity.name);
                stmt.setInt(2, entity.id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE test_entities(id INTEGER PRIMARY KEY, name VARCHAR)");
        }
        dao = new TestEntityDao(connection);
    }

    @Test
    void TC_024_shouldInsertAndFindById() {
        TestEntity entity = new TestEntity(1, "Test Name");
        dao.insert(entity);

        TestEntity found = dao.findById(1);
        assertThat(found).isNotNull();
        assertThat(found.name).isEqualTo("Test Name");
    }

    @Test
    void TC_025_shouldFindAll() {
        dao.insert(new TestEntity(1, "Name1"));
        dao.insert(new TestEntity(2, "Name2"));

        List<TestEntity> all = dao.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void TC_026_shouldDeleteById() {
        dao.insert(new TestEntity(1, "ToDelete"));
        dao.delete(1);

        TestEntity deleted = dao.findById(1);
        assertThat(deleted).isNull();
    }

    //**************************************************************//
    //***********************Exemptions testing*********************//
    //**************************************************************//
    @Test
    void TC_027_shouldReturnNullWhenFindByIdFails() throws SQLException {
        connection.createStatement().execute("DROP TABLE test_entities");

        TestEntity found = dao.findById(1);
        assertThat(found).isNull();
    }

    @Test
    void TC_028_shouldReturnEmptyListWhenFindAllFails() throws SQLException {
        connection.createStatement().execute("DROP TABLE test_entities");

        List<TestEntity> all = dao.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void TC_29_shouldNotThrowWhenDeleteFails() throws SQLException {
        connection.createStatement().execute("DROP TABLE test_entities");

        dao.delete(1);
    }

    @Test
    void shouldNotThrowWhenConnectionClosedFindById() throws SQLException {
        connection.close();

        TestEntity found = dao.findById(1);
        assertThat(found).isNull();
    }

    @Test
    void shouldNotThrowWhenConnectionClosedFindAll() throws SQLException {
        connection.close();

        List<TestEntity> all = dao.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void shouldNotThrowWhenConnectionClosedDelete() throws SQLException {
        connection.close();

        dao.delete(1);
    }
}
