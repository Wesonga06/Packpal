package dao;

import database.DatabaseConfig;
import models.PackingList;
import models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackingListDAO {

    // ✅ Create new packing list
    public boolean createPackingList(PackingList list) {
        String sql = "INSERT INTO packing_lists (user_id, destination, description, start_date, end_date, trip_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, list.getUserId());
            stmt.setString(2, list.getDestination());
            stmt.setString(3, list.getDescription());
            stmt.setDate(4, list.getStartDate());
            stmt.setDate(5, list.getEndDate());
            stmt.setString(6, list.getTripType());

          int rowsInserted = stmt.executeUpdate();
          return rowsInserted > 0;
          
          
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        
                }

    // ✅ Get all packing lists for a specific user
    public List<PackingList> getPackingListsByUser(int userId) {
        List<PackingList> lists = new ArrayList<>();
        String sql = "SELECT * FROM packing_lists WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    PackingList list = new PackingList();
                    list.setListId(rs.getInt("list_id"));
                    list.setUserId(rs.getInt("user_id"));
                    list.setDestination(rs.getString("destination"));
                    list.setDescription(rs.getString("description"));
                    list.setStartDate(rs.getDate("start_date"));
                    list.setEndDate(rs.getDate("end_date"));
                    list.setTripType(rs.getString("trip_type"));
                    lists.add(list);
                }

                   rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
         for(PackingList list : lists){
        int total = getTotalItemsCount(list.getListId());
        int packed = getPackedItemsCount(list.getListId());
        list.setTotalItems(total);
        list.setPackedItemsCount(packed);
    }
         return lists;
}

    // ✅ Get total number of items in a list
    public int getTotalItemsCount(int listId) {
        String sql = "SELECT COUNT(*) AS total FROM items WHERE list_id = ?";
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

    // ✅ Get total number of packed items
    public int getPackedItemsCount(int listId) {
        String sql = "SELECT COUNT(*) AS packed FROM items WHERE list_id = ? AND packed = TRUE";
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

    // ✅ Get all items belonging to a list
    public List<Item> getItemsByListId(int listId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE list_id = ?";

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
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // ✅ Update category of an item
    public boolean updateItemCategory(int itemId, String newCategory) {
        String sql = "UPDATE items SET category = ? WHERE item_id = ?";

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

    // ✅ Mark item as packed/unpacked
    public boolean setItemPackedStatus(int itemId, boolean packed) {
        String sql = "UPDATE items SET packed = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, packed);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ Delete packing list
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

    public void updateItemPackedStatus(int itemId, boolean packed) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
