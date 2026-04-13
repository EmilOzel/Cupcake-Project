package app.persistence;

import app.entities.Cart;
import app.entities.OrderLine;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        Connection conn = null;

        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            double total = cart.getTotal();

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

            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
                ps.setInt(1, customerId);
                ps.setDouble(2, total);

                ResultSet rs = ps.executeQuery();
                rs.next();
                orderId = rs.getInt("order_id");
            }

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
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ignored) {
            }
            throw new DatabaseException("Fejl under betaling", e);

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}