/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import database.DatabaseConfig;
import models.Item;
import models.PackingList;
import java.sql.*;  // Essential: Covers Connection, PreparedStatement, SQLException, etc.

/**
 *
 * @author cindy
 */
public class PackingListDAO {
    // Inner class for packing progress
    public static class PackingProgress {
        private int totalItems;
        private int packedItems;
        
        public PackingProgress(int totalItems, int packedItems) {
            this.totalItems = totalItems;
            this.packedItems = packedItems;
        }
        
        public int getTotalItems() { return totalItems; }
        public int getPackedItems() { return packedItems; }
        
        public int getPercentage() {
            if (totalItems == 0) return 0;
            return (int) ((packedItems * 100.0) / totalItems);
        }
    }
    
    // Create new packing list
    public int createPackingList(PackingList list) {
        String sql = "INSERT INTO packing_lists (user_id, list_name, description, destination, " +
                     "start_date, end_date, trip_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return -1;
            }
            try (PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, list.getUserId());
                pstmt.setString(2, list.getListName());
                pstmt.setString(3, list.getDescription());
                pstmt.setString(4, (String) list.getDestination());
                pstmt.setDate(5, list.getStartDate());
                pstmt.setDate(6, list.getEndDate());
                pstmt.setString(7, list.getTripType());
                
                pstmt.executeUpdate();
                
                try (packpal.dao.ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in createPackingList: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    // Get all packing lists for a user
    public List<PackingList> getPackingListsByUser(int userId) {
        List<PackingList> lists = new ArrayList<>();
        String sql = "SELECT * FROM packing_lists WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return lists;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
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
                        list.setCreatedAt(rs.getTimestamp("created_at"));
                        
                        // Load items for this list
                        list.setItems(getItemsByList(list.getListId()));
                        
                        lists.add(list);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getPackingListsByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return lists;
    }
    
    // Get single packing list by ID
    public PackingList getPackingListById(int listId) {
        String sql = "SELECT * FROM packing_lists WHERE list_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return null;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, listId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        PackingList list = new PackingList();
                        list.setListId(rs.getInt("list_id"));
                        list.setUserId(rs.getInt("user_id"));
                        list.setListName(rs.getString("list_name"));
                        list.setDescription(rs.getString("description"));
                        list.setDestination(rs.getString("destination"));
                        list.setStartDate(rs.getDate("start_date"));
                        list.setEndDate(rs.getDate("end_date"));
                        list.setTripType(rs.getString("trip_type"));
                        list.setCreatedAt(rs.getTimestamp("created_at"));
                        
                        // Load items
                        list.setItems(getItemsByList(listId));
                        
                        return list;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getPackingListById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Add item to packing list
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (list_id, item_name, is_packed, category, priority) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, item.getListId());
                pstmt.setString(2, item.getItemName());
                pstmt.setBoolean(3, item.isPacked());
                pstmt.setString(4, item.getCategory());
                pstmt.setInt(5, item.getPriority());
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in addItem: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get all items for a packing list
    public List<Item> getItemsByList(int listId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE list_id = ? ORDER BY priority DESC, item_name";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return items;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, listId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Item item = new Item();
                        item.setItemId(rs.getInt("item_id"));
                        item.setListId(rs.getInt("list_id"));
                        item.setItemName(rs.getString("item_name"));
                        item.setPacked(rs.getBoolean("is_packed"));
                        item.setCategory(rs.getString("category"));
                        item.setPriority(rs.getInt("priority"));
                        item.setCreatedAt(rs.getTimestamp("created_at"));
                        
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getItemsByList: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    // Update item packed status
    public boolean updateItemPackedStatus(int itemId, boolean isPacked) {
        String sql = "UPDATE items SET is_packed = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, isPacked);
                pstmt.setInt(2, itemId);
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in updateItemPackedStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete item
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, itemId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in deleteItem: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete packing list
    public boolean deletePackingList(int listId) {
        // First delete associated items
        String deleteItemsSql = "DELETE FROM items WHERE list_id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                try (PreparedStatement pstmtItems = conn.prepareStatement(deleteItemsSql)) {
                    pstmtItems.setInt(1, listId);
                    pstmtItems.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in deletePackingList (items): " + e.getMessage());
            e.printStackTrace();
        }
        
        // Then delete the list
        String sql = "DELETE FROM packing_lists WHERE list_id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return false;
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, listId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in deletePackingList (list): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get packing progress
    public PackingProgress getPackingProgress(int listId) {
        String sql = "SELECT COUNT(*) as total, " +
                     "SUM(CASE WHEN is_packed = TRUE THEN 1 ELSE 0 END) as packed " +
                     "FROM items WHERE list_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return new PackingProgress(0, 0);
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, listId);
                try (packpal.dao.ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int total = rs.getInt("total");
                        int packed = rs.getInt("packed");
                        return new PackingProgress(total, packed);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getPackingProgress: " + e.getMessage());
            e.printStackTrace();
        }
        return new PackingProgress(0, 0);
    }
}