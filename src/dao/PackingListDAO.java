package dao;

import database.DatabaseConfig;
import models.PackingList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Item;

public class PackingListDAO {

    // Create a new packing list
    public int createPackingList(PackingList list) {
        String sql = "INSERT INTO packing_lists (user_id, list_name, description, destination, start_date, end_date, trip_type) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, list.getUserId());
            stmt.setString(2, list.getListName());
            stmt.setString(3, list.getDescription());
            stmt.setString(4, list.getDestination());
            stmt.setDate(5, list.getStartDate());
            stmt.setDate(6, list.getEndDate());
            stmt.setString(7, list.getTripType());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // return new list_id
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // failed
    }

    // Get all packing lists by user ID
    public List<PackingList> getPackingListsByUser(int userId) {
        List<PackingList> lists = new ArrayList<>();
        String sql = "SELECT * FROM packing_lists WHERE user_id = ? ORDER BY start_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PackingList list = new PackingList();
                    list.setListId(rs.getInt("list_id"));
                    list.setUserId(rs.getInt("user_id"));
                    list.setListName(rs.getString("list_name"));
                    list.setDescription(rs.getString("description"));
                    list.setDestination(rs.getString("destination"));
                    list.setStartDate(rs.getDate("start_date"));
                    list.setEndDate(rs.getDate("end_date"));
                    list.setTripType(rs.getString("trip_type"));
                    lists.add(list);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    // Get a single packing list by ID
    public PackingList getPackingListById(int listId) {
        String sql = "SELECT * FROM packing_lists WHERE list_id = ?";
        PackingList list = null;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    list = new PackingList();
                    list.setListId(rs.getInt("list_id"));
                    list.setUserId(rs.getInt("user_id"));
                    list.setListName(rs.getString("list_name"));
                    list.setDescription(rs.getString("description"));
                    list.setDestination(rs.getString("destination"));
                    list.setStartDate(rs.getDate("start_date"));
                    list.setEndDate(rs.getDate("end_date"));
                    list.setTripType(rs.getString("trip_type"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update packing list
    public boolean updatePackingList(PackingList list) {
        String sql = "UPDATE packing_lists SET list_name = ?, description = ?, destination = ?, start_date = ?, end_date = ?, trip_type = ? WHERE list_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, list.getListName());
            stmt.setString(2, list.getDescription());
            stmt.setString(3, list.getDestination());
            stmt.setDate(4, list.getStartDate());
            stmt.setDate(5, list.getEndDate());
            stmt.setString(6, list.getTripType());
            stmt.setInt(7, list.getListId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete packing list
    public boolean deletePackingList(int listId) {
        String sql = "DELETE FROM packing_lists WHERE list_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addItem(Item item) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void deleteItem(int itemId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void updateItemPackedStatus(int itemId, boolean selected) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}



