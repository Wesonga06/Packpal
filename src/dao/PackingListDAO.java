package dao;

import database.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.PackingList;

public class PackingListDAO {

    private static final int USER_ID = 1; // Temporary demo user

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    // ✅ Initialize schema
    public void initSchema() {
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

    // ✅ Create new packing list (used by CreateNewListView)
    public int createPackingList(String name, String dest, String dates, String type) {
        int listId = -1;
        String sql = """
            INSERT INTO packing_lists (user_id, list_name, description, destination, start_date, end_date, trip_type)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, USER_ID);
            stmt.setString(2, name);
            stmt.setString(3, "Auto-generated list for " + dest);
            stmt.setString(4, dest);

            // Convert dd/mm/yyyy → yyyy-mm-dd
            java.sql.Date sqlDate = null;
            try {
                String[] parts = dates.split("/");
                if (parts.length == 3) {
                    String formatted = parts[2] + "-" + parts[1] + "-" + parts[0];
                    sqlDate = java.sql.Date.valueOf(formatted);
                }
            } catch (Exception ex) {
                sqlDate = new java.sql.Date(System.currentTimeMillis());
            }

            stmt.setDate(5, sqlDate);
            stmt.setDate(6, sqlDate);
            stmt.setString(7, type);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) listId = rs.getInt(1);

            if (listId > 0) addDefaultItems(listId, type);

        } catch (SQLException e) {
            System.err.println("❌ Error creating packing list: " + e.getMessage());
            e.printStackTrace();
        }

        return listId;
    }

    // ✅ Overload for PackingList object (used by some classes)
    public int createPackingList(PackingList list) {
        return createPackingList(
                list.getListName(),
                list.getDestination(),
                list.getStartDate() != null ? list.getStartDate().toString() : "01/01/2025",
                list.getTripType()
        );
    }

    // ✅ Retrieve all packing lists
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
        }

        return lists;
    }

    // ✅ Add default items based on trip type
    private void addDefaultItems(int listId, String type) {
        String[] defaults = switch (type) {
            case "Beach" -> new String[]{"Swimsuit", "Towel", "Sunscreen"};
            case "Business" -> new String[]{"Laptop", "Notebook", "Business Cards"};
            case "Camping" -> new String[]{"Tent", "Sleeping Bag", "Flashlight"};
            case "Weekend" -> new String[]{"Casual Clothes", "Snacks", "Toothbrush"};
            default -> new String[]{"Essentials", "Clothes", "Toiletries"};
        };

        String sql = "INSERT INTO list_items (list_id, name) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String item : defaults) {
                stmt.setInt(1, listId);
                stmt.setString(2, item);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding default items: " + e.getMessage());
        }
    }

    // ✅ Retrieve all items in a list
    public List<ListItem> getListItems(int listId) {
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
            System.err.println("❌ Error retrieving list items: " + e.getMessage());
        }

        return items;
    }

    // ✅ Alias for compatibility (used by ListDetailView)
    public List<ListItem> getItemByListId(int listId) {
        return getListItems(listId);
    }

    // ✅ Retrieve single item
    public ListItem getListItem(int itemId) {
        String sql = "SELECT id, name, packed FROM list_items WHERE id = ?";
        ListItem item = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                item = new ListItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("packed")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving list item: " + e.getMessage());
        }

        return item;
    }

    // ✅ Update packed status
    public void setItemPackedStatus(int itemId, boolean packed) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE list_items SET packed = ? WHERE id = ?")) {
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

    // ✅ Count totals
    public int getTotalItemsCount(int listId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM list_items WHERE list_id = ?")) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Error getting total items count: " + e.getMessage());
        }
        return 0;
    }

    public int getPackedItemsCount(int listId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM list_items WHERE list_id = ? AND packed = 1")) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Error getting packed items count: " + e.getMessage());
        }
        return 0;
    }

    // ✅ Inner model class for list items
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
        public boolean isPacked() { return packed; }

        public void setName(String name) { this.name = name; }
        public void setPacked(boolean packed) { this.packed = packed; }
    }
}

