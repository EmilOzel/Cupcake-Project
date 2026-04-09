package app.persistence;

import app.entities.Bottom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BottomMapper {

    public static Bottom getBottom(ConnectionPool connectionPool, int bottomId) {

        String sql = "SELECT * FROM bottoms WHERE bottom_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bottomId);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return new Bottom(
                        rs.getInt("bottom_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    // BottomMapper.java — tilføj begge metoder
    public static List<Bottom> getAllBottoms(ConnectionPool connectionPool) {
        List<Bottom> bottoms = new ArrayList<>();
        String sql = "SELECT * FROM bottoms";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bottoms.add(new Bottom(
                        rs.getInt("bottom_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bottoms;
    }

    public static Bottom getBottomByName(ConnectionPool connectionPool, String name) {
        String sql = "SELECT * FROM bottoms WHERE name = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Bottom(
                        rs.getInt("bottom_id"),
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
