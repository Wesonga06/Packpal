package dao;

import models.PackingList;
import models.Item;
import database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackingListDAO {

    // ✅ Insert a new packing list and return generated ID
    public int createPackingList(PackingList list) {
        String sql = "INSERT INTO packing_lists (user_id, list_name, description, destination, trip_type, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, list.getUserId());
            stmt.setString(2, list.getListName());
            stmt.setString(3, list.getDescription());
            stmt.setString(4, list.getDestination());
            stmt.setString(5, list.getTripType());
            stmt.setDate(6, list.getStartDate());
            stmt.setDate(7, list.getEndDate());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    // ✅ Fetch all packing lists for a specific user
    public List<PackingList> getPackingListsByUser(int userId) {
        List<PackingList> lists = new ArrayList<>();
        String sql = "SELECT * FROM packing_lists WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PackingList list = new PackingList();
                list.setListId(rs.getInt("list_id"));
                list.setUserId(rs.getInt("user_id"));
                list.setListName(rs.getString("list_name"));
                list.setDestination(rs.getString("destination"));
                list.setDescription(rs.getString("description"));
                list.setTripType(rs.getString("trip_type"));
                list.setStartDate(rs.getDate("start_date"));
                list.setEndDate(rs.getDate("end_date"));
                lists.add(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    // ✅ Fetch one list by ID
    public PackingList getPackingListById(int listId) {
        String sql = "SELECT * FROM packing_lists WHERE list_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PackingList list = new PackingList();
                list.setListId(rs.getInt("list_id"));
                list.setUserId(rs.getInt("user_id"));
                list.setListName(rs.getString("list_name"));
                list.setDestination(rs.getString("destination"));
                list.setDescription(rs.getString("description"));
                list.setTripType(rs.getString("trip_type"));
                list.setStartDate(rs.getDate("start_date"));
                list.setEndDate(rs.getDate("end_date"));
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Delete a packing list
    public boolean deletePackingList(int listId) {
        String sql = "DELETE FROM packing_lists WHERE list_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Insert a new item into a list
    public boolean addItemToList(Item item) {
        String sql = "INSERT INTO items (list_id, item_name, category, packed) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, item.getListId());
            stmt.setString(2, item.getItemName());
            stmt.setString(3, item.getCategory());
            stmt.setBoolean(4, item.isPacked());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Get all items in a list
    public List<Item> getItemsByListId(int listId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE list_id = ? ORDER BY item_id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setListId(rs.getInt("list_id"));
                item.setItemName(rs.getString("item_name"));
                item.setCategory(rs.getString("category"));
                item.setPacked(rs.getBoolean("packed"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // ✅ Update packed/unpacked status
    public void setItemPackedStatus(int itemId, boolean isPacked) {
        String sql = "UPDATE items SET packed = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isPacked);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Delete an item
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Get total number of items in a list
    public int getTotalItemsCount(int listId) {
        String sql = "SELECT COUNT(*) FROM items WHERE list_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ Get packed items count
    public int getPackedItemsCount(int listId) {
        String sql = "SELECT COUNT(*) FROM items WHERE list_id = ? AND packed = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}