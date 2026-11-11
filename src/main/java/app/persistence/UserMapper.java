package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.nio.charset.StandardCharsets;
import org.mindrot.jbcrypt.BCrypt;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        if (email == null || password == null || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new DatabaseException("Forkert email eller kodeord");
        }
        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && matchesPassword(password, rs.getString("password"))) {
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
        if (email == null || email.length() > 100 || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new DatabaseException("Indtast en gyldig email");
        }
        if (password == null || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new DatabaseException("Adgangskoden skal være mindst 8 tegn og højst 72 bytes");
        }
        double initialBalance = "true".equals(System.getenv("DEMO_MODE")) ? 100 : 0;
        String sql = "INSERT INTO customers (email, password, balance, role) VALUES (?, ?, ?, 'user')";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt(10)));
            ps.setDouble(3, initialBalance);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Email findes allerede", e);
        }
    }

    private static boolean matchesPassword(String password, String stored) {
        if (stored == null) return false;
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return BCrypt.checkpw(password, stored);
        }
        return java.security.MessageDigest.isEqual(
                password.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
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