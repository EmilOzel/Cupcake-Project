package app.persistence;

import app.entities.Cart;
import app.entities.OrderLine;
import app.exceptions.DatabaseException;

import java.sql.*;

public class CheckoutMapper {

    public static int placeOrder(int customerId, Cart cart, ConnectionPool connectionPool) throws DatabaseException {

        String deductBalance = "UPDATE customers SET balance = balance - ? WHERE customer_id = ? AND balance >= ?";
        String insertOrder = "INSERT INTO orders (customer_id, total_price) VALUES (?, ?) RETURNING order_id";
        String insertOrderLine = """
            INSERT INTO order_lines (order_id, bottom_id, topping_id, quantity)
            VALUES (?, 
                (SELECT bottom_id FROM bottoms WHERE name = ?),
                (SELECT topping_id FROM toppings WHERE name = ?),
                ?)
            """;

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false); // Start transaktion

            double total = cart.getTotal();

            // 1. Træk beløb fra saldo
            try (PreparedStatement ps = conn.prepareStatement(deductBalance)) {
                ps.setDouble(1, total);
                ps.setInt(2, customerId);
                ps.setDouble(3, total);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    throw new DatabaseException("Ikke nok penge på kontoen");
                }
            }

            // 2. Opret ordre
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
                ps.setInt(1, customerId);
                ps.setDouble(2, total);
                ResultSet rs = ps.executeQuery();
                rs.next();
                orderId = rs.getInt("order_id");
            }

            // 3. Indsæt order lines
            try (PreparedStatement ps = conn.prepareStatement(insertOrderLine)) {
                for (OrderLine line : cart.getOrderLines()) {
                    ps.setInt(1, orderId);
                    ps.setString(2, line.getBottom());
                    ps.setString(3, line.getTopping());
                    ps.setInt(4, line.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl under betaling", e);
        }
    }
}