package models;

import java.sql.Timestamp;

public class Item {
    private int itemId;
    private int listId;
    private String itemName;
    private String category;
    private boolean isPacked;
    private int priority;
    private Timestamp createdAt;

    // Full constructor
    public Item(int listId, String itemName, String category, boolean isPacked, int priority, Timestamp createdAt) {
        this.listId = listId;
        this.itemName = itemName;
        this.category = category;
        this.isPacked = isPacked;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    // Empty constructor
    public Item() {}

    // ===========================
    // Getters
    // ===========================
    public int getItemId() { return itemId; }
    public int getListId() { return listId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public boolean isPacked() { return isPacked; }
    public int getPriority() { return priority; }
    public Timestamp getCreatedAt() { return createdAt; }

    // ===========================
    // Setters
    // ===========================
    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setListId(int listId) { this.listId = listId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setCategory(String category) { this.category = category; }
    public void setPacked(boolean isPacked) { this.isPacked = isPacked; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // ===========================
    // Utility
    // ===========================
    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", listId=" + listId +
                ", itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", isPacked=" + isPacked +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}

