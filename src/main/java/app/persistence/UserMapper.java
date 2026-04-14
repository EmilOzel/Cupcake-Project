package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;

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
    public static User register(String email, String password, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO customers (email, password, balance) VALUES (?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setDouble(3, 0.0); // start balance

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new User(id, email, password, 0.0);
                } else {
                    throw new DatabaseException("Kunne ikke hente genereret ID");
                }
            } else {
                throw new DatabaseException("Kunne ikke oprette bruger");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl ved oprettelse", e);
        }
    }

}