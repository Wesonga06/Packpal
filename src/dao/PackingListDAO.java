package dao;

import models.PackingList;
import models.PackingProgress;
import models.Item;
import database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackingListDAO {

    // 🟩 Create a new packing list and return the generated ID
    public int createPackingList(PackingList list) {
        String sql = "INSERT INTO packing_lists (user_id, list_name, description, destination, start_date, end_date, trip_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("❌ Error: Database connection is null");
                return -1;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, list.getUserId());
                pstmt.setString(2, list.getListName());
                pstmt.setString(3, list.getDescription());
                pstmt.setString(4, list.getDestination());
                pstmt.setDate(5, list.getStartDate());
                pstmt.setDate(6, list.getEndDate());
                pstmt.setString(7, list.getTripType());

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating packing list: " + e.getMessage());
        }
        return -1;
    }

    // 🟩 Retrieve all packing lists for a given user
    public List<PackingList> getPackingListsByUserId(int userId) {
        List<PackingList> lists = new ArrayList<>();
        String sql = "SELECT * FROM packing_lists WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PackingList list = new PackingList(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("list_name"),
                        rs.getString("description"),
                        rs.getString("destination"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("trip_type")
                    );
                    lists.add(list);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching packing lists: " + e.getMessage());
        }

        return lists;
    }

    // ✅ Alias method for flexibility (same as getPackingListsByUserId)
    public List<PackingList> getPackingListsByUser(int userId) {
        return getPackingListsByUserId(userId);
    }

    // 🟩 Retrieve a single packing list by its ID
    public PackingList getPackingListById(int listId) {
        String sql = "SELECT * FROM packing_lists WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, listId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PackingList(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("list_name"),
                        rs.getString("description"),
                        rs.getString("destination"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("trip_type")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting packing list by ID: " + e.getMessage());
        }
        return null;
    }

    // 🟩 Retrieve packing progress for a given list
    public PackingProgress getPackingProgress(int listId) {
        String sql = """
            SELECT 
                COUNT(*) AS total,
                SUM(CASE WHEN packed = true THEN 1 ELSE 0 END) AS packed
            FROM packing_items
            WHERE list_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, listId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int packed = rs.getInt("packed");
                    return new PackingProgress(total, packed);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching packing progress: " + e.getMessage());
        }

        return new PackingProgress(0, 0);
    }

    // 🟩 Add an item to a list
    public boolean addItem(Item item) {
        String sql = "INSERT INTO packing_items (list_id, item_name, category, packed, priority, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getListId());
            pstmt.setString(2, item.getItemName());
            pstmt.setString(3, item.getCategory());
            pstmt.setBoolean(4, item.isPacked());
            pstmt.setInt(5, item.getPriority());
            pstmt.setTimestamp(6, item.getCreatedAt());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error adding item: " + e.getMessage());
        }
        return false;
    }

    // 🟩 Update packed status of an item
    public void updateItemPackedStatus(int itemId, boolean packed) {
        String sql = "UPDATE packing_items SET packed = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, packed);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error updating packed status: " + e.getMessage());
        }
    }

    // 🟩 Delete an item by ID
    public void deleteItem(int itemId) {
        String sql = "DELETE FROM packing_items WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error deleting item: " + e.getMessage());
        }
    }

    // 🟩 Delete a packing list by ID
    public boolean deletePackingList(int listId) {
        String sql = "DELETE FROM packing_lists WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, listId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting packing list: " + e.getMessage());
        }
        return false;
    }
}


