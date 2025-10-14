package dao;

import database.DatabaseConfig;
import models.PackingList;
import models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackingListDAO {

    // ===========================
    // Packing List Methods
    // ===========================

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
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

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

                    // --- NEW: Calculate and attach progress stats ---
                    int total = getTotalItemsCount(list.getListId());
                    int packed = getPackedItemsCount(list.getListId());
                    list.setTotalItems(total);
                    list.setPackedItemsCount(packed);

                    lists.add(list);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

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

                    // --- Include live progress stats ---
                    list.setTotalItems(getTotalItemsCount(listId));
                    list.setPackedItemsCount(getPackedItemsCount(listId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updatePackingList(PackingList list) {
        String sql = "UPDATE packing_lists SET list_name=?, description=?, destination=?, start_date=?, end_date=?, trip_type=? WHERE list_id=?";

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

    public boolean deletePackingList(int listId) {
        String sql = "DELETE FROM packing_lists WHERE list_id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===========================
    // Item Methods
    // ===========================

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (list_id, item_name, category, packed, priority, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getListId());
            stmt.setString(2, item.getItemName());
            stmt.setString(3, item.getCategory());
            stmt.setBoolean(4, item.isPacked());
            stmt.setInt(5, item.getPriority());
            stmt.setTimestamp(6, item.getCreatedAt());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getItemIdByNameAndList(String itemName, int listId) {
        String sql = "SELECT item_id FROM items WHERE item_name=? AND list_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemName);
            stmt.setInt(2, listId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateItemCategory(int itemId, String newCategory) {
        String sql = "UPDATE items SET category=? WHERE item_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newCategory);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItemPackedStatus(int itemId, boolean packed) {
        String sql = "UPDATE items SET packed=? WHERE item_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, packed);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Item> getItemsByListId(int listId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE list_id=? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setItemId(rs.getInt("item_id"));
                    item.setListId(rs.getInt("list_id"));
                    item.setItemName(rs.getString("item_name"));
                    item.setCategory(rs.getString("category"));
                    item.setPacked(rs.getBoolean("packed"));
                    item.setPriority(rs.getInt("priority"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // ===========================
    // NEW METHODS
    // ===========================

    /** Get total number of items in a list */
    public int getTotalItemsCount(int listId) {
        String sql = "SELECT COUNT(*) AS total FROM items WHERE list_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Get number of packed items in a list */
    public int getPackedItemsCount(int listId) {
        String sql = "SELECT COUNT(*) AS packed FROM items WHERE list_id=? AND packed=TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("packed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
