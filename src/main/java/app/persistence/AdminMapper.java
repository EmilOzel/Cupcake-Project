package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminMapper {

    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("customer_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getDouble("balance"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente brugere", e);
        }
        return users;
    }

    public static void deleteUser(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke slette bruger", e);
        }
    }

    public static void addBalance(int userId, double amount, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE customers SET balance = balance + ? WHERE customer_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke tilføje credits", e);
        }
    }

    public static List<Object[]> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Object[]> orders = new ArrayList<>();
        String sql = """
            SELECT o.order_id, c.email, o.total_price, o.status
            FROM orders o
            JOIN customers c ON o.customer_id = c.customer_id
            ORDER BY o.order_id DESC
            """;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("email"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrer", e);
        }
        return orders;
    }

    public static void deleteOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        // Slet order_lines først pga. foreign key
        String deleteLines = "DELETE FROM order_lines WHERE order_id = ?";
        String deleteOrder = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(deleteLines);
             PreparedStatement ps2 = conn.prepareStatement(deleteOrder)) {

            ps1.setInt(1, orderId);
            ps1.executeUpdate();

            ps2.setInt(1, orderId);
            ps2.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke slette ordre", e);
        }
    }

    public static void updateOrderStatus(int orderId, String status, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke opdatere status", e);
        }
    }
}