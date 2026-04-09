package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM customers WHERE email = ? AND password = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("customer_id");
                String userEmail = rs.getString("email");
                String userPassword = rs.getString("password");
                double balance = rs.getDouble("balance");

                return new User(id, userEmail, userPassword, balance);
            } else {
                throw new DatabaseException("Forkert email eller kodeord");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl", e);
        }
    }
    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO customers (email, password, balance) VALUES (?, ?, 0)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                throw new DatabaseException("Email er allerede i brug");
            }
            throw new DatabaseException("Databasefejl ved oprettelse", e);
        }
    }
}