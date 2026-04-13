package app.persistence;

import app.entities.Topping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ToppingMapper {

    public static List<Topping> getAllToppings(ConnectionPool connectionPool) {
        List<Topping> toppings = new ArrayList<>();
        String sql = "SELECT * FROM toppings";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                toppings.add(new Topping(
                        rs.getInt("topping_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    public static Topping getToppingByName(ConnectionPool connectionPool, String name) {
        String sql = "SELECT * FROM toppings WHERE name = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Topping(
                        rs.getInt("topping_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
