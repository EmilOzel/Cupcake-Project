package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM customers WHERE email = ? AND password = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("customer_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getDouble("balance"),
                        rs.getString("role")
                );
            } else {
                throw new DatabaseException("Forkert email eller kodeord");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl", e);
        }
    }

    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO customers (email, password, balance, role) VALUES (?, ?, 0, 'user')";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Email findes allerede", e);
        }
    }

    public static User getUserById(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        userId,
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getDouble("balance"),
                        rs.getString("role")
                );
            } else {
                throw new DatabaseException("Bruger ikke fundet");
            }

        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e);
        }
    }
}