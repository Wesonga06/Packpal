package dao;

import models.PackingList;
import models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackingListDAO {

    private Connection conn;

    // ✅ Constructor establishes a SQLite connection
    public PackingListDAO() {
        try {
            // Adjust the path or DB name if needed
            conn = DriverManager.getConnection("jdbc:sqlite:packpal.db");
            createTablesIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initSchema() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS packing_lists (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    destination TEXT,
                    dates TEXT,
                    type TEXT
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS list_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    list_id INTEGER,
                    name TEXT,
                    quantity INTEGER,
                    category TEXT,
                    FOREIGN KEY(list_id) REFERENCES packing_lists(id)
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Ensure tables exist (packing_lists + items)
    private void createTablesIfNotExist() {
        String createPackingListsTable = """
            CREATE TABLE IF NOT EXISTS packing_lists (
                list_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                list_name TEXT,
                destination TEXT,
                description TEXT,
                date_created TEXT DEFAULT CURRENT_TIMESTAMP
            );
        """;

        String createItemsTable = """
            CREATE TABLE IF NOT EXISTS items (
                item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                list_id INTEGER,
                item_name TEXT,
                category TEXT,
                is_packed BOOLEAN DEFAULT 0,
                FOREIGN KEY (list_id) REFERENCES packing_lists(list_id) ON DELETE CASCADE
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createPackingListsTable);
            stmt.execute(createItemsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Insert a new packing list
 'public int createPackingList(PackingList list) {
    String sql = "INSERT INTO packing_lists (user_id, list_name, description, destination, trip_type, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
    int generatedId = -1;

    try (Connection conn = DatabaseConnection.getConnection();
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
                    generatedId = rs.getInt(1); // The auto-generated ID
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return generatedId; // Will return the new list ID, or -1 if failed
}
'
    // ✅ Fetch all packing lists for a specific user
    public List<PackingList> getPackingListsByUser(int userId) {
        List<PackingList> lists = new ArrayList<>();
        String sql = "SELECT * FROM packing_lists WHERE user_id = ? ORDER BY date_created DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PackingList list = new PackingList();
                list.setListId(rs.getInt("list_id"));
                list.setUserId(rs.getInt("user_id"));
                list.setListName(rs.getString("list_name"));
                list.setDestination(rs.getString("destination"));
                list.setDescription(rs.getString("description"));
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PackingList list = new PackingList();
                list.setListId(rs.getInt("list_id"));
                list.setUserId(rs.getInt("user_id"));
                list.setListName(rs.getString("list_name"));
                list.setDestination(rs.getString("destination"));
                list.setDescription(rs.getString("description"));
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "INSERT INTO items (list_id, item_name, category, is_packed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setListId(rs.getInt("list_id"));
                item.setItemName(rs.getString("item_name"));
                item.setCategory(rs.getString("category"));
                item.setPacked(rs.getBoolean("is_packed"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // ✅ Update packed/unpacked status
    public void setItemPackedStatus(int itemId, boolean isPacked) {
        String sql = "UPDATE items SET is_packed = ? WHERE item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT COUNT(*) FROM items WHERE list_id = ? AND is_packed = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ Implemented version instead of UnsupportedOperationException
    public int createPackingList(String name, String dest, String dates, String type) {
        String sql = "INSERT INTO packing_lists (name, destination, dates, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, dest);
            stmt.setString(3, dates);
            stmt.setString(4, type);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<ListItem> getListItems(int listId) {
        List<ListItem> items = new ArrayList<>();
        String sql = "SELECT * FROM list_items WHERE list_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ListItem item = new ListItem(
                        rs.getInt("id"),
                        rs.getInt("list_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("category")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // ✅ Close DB connection (non-static)
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Nested ListItem class
    public static class ListItem {
        private int id;
        private int listId;
        private String name;
        private int quantity;
        private String category;
        private boolean packed;

        public ListItem(int id, int listId, String name, int quantity, String category) {
            this.id = id;
            this.listId = listId;
            this.name = name;
            this.quantity = quantity;
            this.category = category;
        }

        public int getId() { return id; }
        public int getListId() { return listId; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public String getCategory() { return category; }
        public boolean isPacked() { return packed; }

        public void setPacked(boolean packed) { this.packed = packed; }
        public void setName(String newName) { this.name = newName; }

        @Override
        public String toString() {
            return name + " (" + quantity + ") - " + category;
        }
    }
}

    public void setItemPackedStatus(int itemId, boolean selected) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setItemPackedStatus(int itemId, boolean selected) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getTotalItemsCount(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getPackedItemsCount(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static class ListItem {

        public ListItem() {
        }
    }

    public int getPackedItemsCount(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getTotalItemsCount(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Item> getItemsByListId(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Item> getItemsByListId(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
