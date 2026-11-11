package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static List<Object[]> getOrdersByUser(int userId, ConnectionPool connectionPool) throws DatabaseException {
        List<Object[]> orders = new ArrayList<>();
        String sql = """
                SELECT order_id, total_price, status
                FROM orders
                WHERE customer_id = ?
                ORDER BY order_id DESC
                """;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrer", e);
        }
        return orders;
    }

    public static List<Object[]> getOrderLinesForUser(int orderId, int userId, ConnectionPool pool) throws DatabaseException {
        try (Connection conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM orders WHERE order_id = ? AND customer_id = ?")) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DatabaseException("Ordren findes ikke");
            }
        } catch (SQLException error) {
            throw new DatabaseException("Kunne ikke hente ordren", error);
        }
        return getOrderLines(orderId, pool);
    }

    public static List<Object[]> getOrderLines(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        List<Object[]> lines = new ArrayList<>();
        String sql = """
                SELECT b.name AS bottom, t.name AS topping, ol.quantity
                FROM order_lines ol
                JOIN bottoms b ON ol.bottom_id = b.bottom_id
                JOIN toppings t ON ol.topping_id = t.topping_id
                WHERE ol.order_id = ?
                """;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lines.add(new Object[]{
                        rs.getString("bottom"),
                        rs.getString("topping"),
                        rs.getInt("quantity")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrelinjer", e);
        }
        return lines;
    }
}