package dao;

import database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Item;

public class PackingListDAO {
    private static final int USER_ID = 1;  // Hardcoded user ID; extend with authentication

    // Use your DatabaseConfig for connection
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();  // Adjust if your config uses a different method (e.g., getInstance().getConnection())
    }

    // Initialize database schema if not exists
    public void initSchema() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Users table (for future auth)
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, email TEXT UNIQUE, password TEXT, name TEXT)");
            
            // Packing Lists table
            stmt.execute("CREATE TABLE IF NOT EXISTS packing_lists (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, name TEXT, destination TEXT, dates TEXT, type TEXT, created_date DATE DEFAULT CURRENT_DATE, FOREIGN KEY(user_id) REFERENCES users(id))");
            
            // List Items table
            stmt.execute("CREATE TABLE IF NOT EXISTS list_items (id INTEGER PRIMARY KEY AUTOINCREMENT, list_id INTEGER, name TEXT, packed BOOLEAN DEFAULT FALSE, FOREIGN KEY(list_id) REFERENCES packing_lists(id))");
            
            // Insert sample user if none
            stmt.execute("INSERT OR IGNORE INTO users (id, email, password, name) VALUES (1, 'user@example.com', 'pass', 'John Doe')");
            
            // Insert sample data if empty
            stmt.execute("INSERT OR IGNORE INTO packing_lists (id, user_id, name, destination, dates, type) VALUES (1, 1, 'Weekend Getaway', 'Beach', '2025-10-15 to 2025-10-17', 'Weekend')");
            stmt.execute("INSERT OR IGNORE INTO list_items (list_id, name) VALUES (1, 'Passport'), (1, 'Phone Charger'), (1, 'Sunglasses'), (1, 'Toothbrush')");
            
            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create a new packing list
    public int createPackingList(String name, String destination, String dates, String type) {
        if (name == null || name.trim().isEmpty() || destination == null || destination.trim().isEmpty() ||
            dates == null || dates.trim().isEmpty() || type == null) {
            System.err.println("Invalid input parameters for createList");
            return -1;
        }
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO packing_lists (user_id, name, destination, dates, type) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, USER_ID);
            stmt.setString(2, name.trim());
            stmt.setString(3, destination.trim());
            stmt.setString(4, dates.trim());
            stmt.setString(5, type);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Creating list failed, no rows affected.");
                return -1;
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int listId = generatedKeys.getInt(1);
                    addDefaultItems(listId, type);
                    return listId;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in createList: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Read all lists for the user
    public List<PackingList> getLists() {
        List<PackingList> lists = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT pl.id, pl.name, pl.destination, COUNT(li.id) as item_count, SUM(CASE WHEN li.packed = 1 THEN 1 ELSE 0 END) as packed_count, pl.type " +
                "FROM packing_lists pl LEFT JOIN list_items li ON pl.id = li.list_id WHERE pl.user_id = ? GROUP BY pl.id")) {
            stmt.setInt(1, USER_ID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int itemCount = rs.getInt("item_count");
                    int packedCount = rs.getInt("packed_count");
                    String status = (packedCount > 0) ? (packedCount == itemCount ? "packed" : "in progress") : 
                                    rs.getString("type").equals("Template") ? "Template" : "Shared";
                    String subtitle = itemCount + " items - " + status;
                    lists.add(new PackingList(rs.getInt("id"), rs.getString("name"), subtitle));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in getLists: " + e.getMessage());
            e.printStackTrace();
        }
        return lists;
    }

    // Update packed status of an item
    public void updateItemPacked(int listId, int itemId, boolean packed) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE list_items SET packed = ? WHERE list_id = ? AND id = ?")) {
            stmt.setBoolean(1, packed);
            stmt.setInt(2, listId);
            stmt.setInt(3, itemId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No rows updated for item ID " + itemId + " in list ID " + listId);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in updateItemPacked: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get items for a specific list
    public List<ListItem> getListItems(int listId) {
        List<ListItem> items = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name, packed FROM list_items WHERE list_id = ?")) {
            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new ListItem(rs.getInt("id"), rs.getString("name"), rs.getBoolean("packed")));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in getListItems: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    // Add default items based on list type
    private void addDefaultItems(int listId, String type) {
        String[] defaults = switch (type) {
            case "Beach" -> new String[]{"Swimsuit", "Towel", "Sunscreen"};
            case "Business" -> new String[]{"Laptop", "Notebook", "Business Cards"};
            case "Camping" -> new String[]{"Tent", "Sleeping Bag", "Flashlight"};
            case "Weekend" -> new String[]{"Casual Clothes", "Snacks", "Toothbrush"};
            default -> new String[]{"Essentials", "Clothes", "Toiletries"};
        };
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO list_items (list_id, name) VALUES (?, ?)")) {
            for (String item : defaults) {
                stmt.setInt(1, listId);
                stmt.setString(2, item);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in addDefaultItems: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Add item to list
public void addItem(int listId, String name) {
    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO list_items (list_id, name) VALUES (?, ?)")) {
        stmt.setInt(1, listId);
        stmt.setString(2, name.trim());
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.err.println("SQL Exception in addItem: " + e.getMessage());
        e.printStackTrace();
    }
}

// Update item name
public void updateItemName(int listId, int itemId, String newName) {
    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
            "UPDATE list_items SET name = ? WHERE list_id = ? AND id = ?")) {
        stmt.setString(1, newName.trim());
        stmt.setInt(2, listId);
        stmt.setInt(3, itemId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.err.println("SQL Exception in updateItemName: " + e.getMessage());
        e.printStackTrace();
    }
}

// Delete item
public void deleteItem(int listId, int itemId) {
    try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
            "DELETE FROM list_items WHERE list_id = ? AND id = ?")) {
        stmt.setInt(1, listId);
        stmt.setInt(2, itemId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.err.println("SQL Exception in deleteItem: " + e.getMessage());
        e.printStackTrace();
    }
}

    public void setItemPackedStatus(int itemId, boolean packed) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE list_items SET packed =? WHERE id = ?")){
            stmt.setBoolean(1, packed);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e){
            System.err.println("SetItemPackedStatus error: " + e.getMessage());
        }
    }

    public List<ListItem> getItemsByListId(int listId) {
        List<ListItem> item = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name, packed FROM list_items WHERE list_id = ?")) {
            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    item.add(new ListItem(rs.getInt("id"), rs.getString("name"), rs.getBoolean("packed")));
                }
            }
        } catch (SQLException e) {
            System.err.println("GetItemsByListId error: " + e.getMessage());
        }
        return item;
    }

    public int getTotalItemsCount(int listId) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM list_items WHERE list_id = ?")) {
            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("GetTotalItemsCount error: " + e.getMessage());
        }
        return 0;
    }

    public int getPackedItemsCount(int listId) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM list_items WHERE list_id = ? AND packed = 1")) {
            stmt.setInt(1, listId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("GetPackedItemsCount error: " + e.getMessage());
        }
        return 0;
    }


    // POJO classes
    public static class PackingList {
        private final int id;
        private final String name;
        private final String subtitle;

        public PackingList(int id, String name, String subtitle) {
            this.id = id;
            this.name = name;
            this.subtitle = subtitle;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getSubtitle() { return subtitle; }
    }

    public static class ListItem {
        private int id;
        private String name;
        private boolean packed;

        public ListItem(int id, String name, boolean packed) {
            this.id = id;
            this.name = name;
            this.packed = packed;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String newName){
            if(newName != null && !newName.trim().isEmpty()){
                this.name = newName.trim();
            } else {
              throw new IllegalArgumentException("Name cannot be null or empty");
            }
        }
        public boolean isPacked() { return packed; }
        public void setPacked(boolean packed) { this.packed = packed; }

    }
}