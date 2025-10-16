package dao;

import database.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.PackingList;
import dao.ListItem;

public class PackingListDAO{
    private static final int USER_ID = 1;
    
    private Connection getConnection() throws SQLException{
        return DatabaseConfig.getConnection();
    }
    
    public void initSchema(){
         try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    email VARCHAR(100) UNIQUE,
                    password VARCHAR(100),
                    name VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS packing_lists (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    user_id INTEGER,
                    list_name VARCHAR(100),
                    description TEXT,
                    destination VARCHAR(100),
                    start_date DATE,
                    end_date DATE,
                    trip_type VARCHAR(50),
                    created_date DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS list_items (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    list_id INTEGER,
                    name VARCHAR(100),
                    packed BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (list_id) REFERENCES packing_lists(id)
                )
            """);

            stmt.execute("""
                INSERT IGNORE INTO users (id, email, password, name)
                VALUES (1, 'user@example.com', 'pass', 'John Doe')
            """);

            System.out.println("✅ Schema initialized successfully");

        } catch (SQLException e) {
            System.err.println("❌ Schema initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
     public int createPackingList(PackingList list) {
        int listId = -1;
        String sql = "INSERT INTO packing_lists (user_id, list_name, description, destination, start_date, end_date, trip_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, list.getUserId());
            stmt.setString(2, list.getListName());
            stmt.setString(3, list.getDescription());
            stmt.setString(4, list.getDestination());
            stmt.setDate(5, list.getStartDate());
            stmt.setDate(6, list.getEndDate());
            stmt.setString(7, list.getTripType());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    listId = rs.getInt(1);
                }
            }

            // Add default items based on trip type
            if (listId > 0) {
                addDefaultItems(listId, list.getTripType());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error creating packing list: " + e.getMessage());
            e.printStackTrace();
        }

        return listId;
    }

    // ✅ Retrieve all lists for current user
    public List<PackingList> getLists() {
        List<PackingList> lists = new ArrayList<>();

        String sql = """
            SELECT pl.id, pl.list_name, pl.destination,
                   COUNT(li.id) AS item_count,
                   SUM(CASE WHEN li.packed = 1 THEN 1 ELSE 0 END) AS packed_count,
                   pl.trip_type
            FROM packing_lists pl
            LEFT JOIN list_items li ON pl.id = li.list_id
            WHERE pl.user_id = ?
            GROUP BY pl.id
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, USER_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int total = rs.getInt("item_count");
                int packed = rs.getInt("packed_count");
                String status = (packed == total && total > 0)
                        ? "Packed"
                        : (packed > 0 ? "In Progress" : "Not Started");
                String subtitle = total + " items - " + status;

                PackingList list = new PackingList();
                list.setListId(rs.getInt("id"));
                list.setListName(rs.getString("list_name"));
                list.setDestination(rs.getString("destination"));
                list.setTripType(rs.getString("trip_type"));
                list.setSubtitle(subtitle);

                lists.add(list);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting lists: " + e.getMessage());
            e.printStackTrace();
        }

        return lists;
    }

    // ✅ Add default items based on type
    private void addDefaultItems(int listId, String type) {
        String[] defaults = switch (type) {
            case "Beach" -> new String[]{"Swimsuit", "Towel", "Sunscreen"};
            case "Business" -> new String[]{"Laptop", "Notebook", "Business Cards"};
            case "Camping" -> new String[]{"Tent", "Sleeping Bag", "Flashlight"};
            case "Weekend" -> new String[]{"Casual Clothes", "Snacks", "Toothbrush"};
            default -> new String[]{"Essentials", "Clothes", "Toiletries"};
        };

        String sql = "INSERT INTO list_items (list_id, name) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String item : defaults) {
                stmt.setInt(1, listId);
                stmt.setString(2, item);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding default items: " + e.getMessage());
        }
    }

    // ✅ Retrieve list items
    public List<ListItem> getItemsByListId(int listId) {
        List<ListItem> items = new ArrayList<>();
        String sql = "SELECT id, name, packed FROM list_items WHERE list_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new ListItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("packed")
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    // ✅ Update packed status
    public void setItemPackedStatus(int itemId, boolean packed) {
        String sql = "UPDATE list_items SET packed = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, packed);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error updating item packed status: " + e.getMessage());
        }
    }

    // ✅ Delete item
    public void deleteItem(int listId, int itemId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM list_items WHERE list_id = ? AND id = ?")) {
            stmt.setInt(1, listId);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error deleting item: " + e.getMessage());
        }
    }

    public int createPackingList(String name, String dest, String dates, String type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getTotalItemsCount(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getPackedItemsCount(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<ListItem> getListItems(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static class ListItem {

        public ListItem() {
        }

        private ListItem(int aInt, String string, boolean aBoolean) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public boolean isPacked() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public String getName() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public int getId() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public void setName(String newName) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public void setPacked(boolean selected) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
