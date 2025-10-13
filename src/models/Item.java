package models;

import java.sql.Timestamp;

/**
 * Represents a single item in a packing list.
 * Author: Cindy
 */
public class Item {
    private int itemId;
    private int listId;
    private String itemName;
    private String category;
    private boolean isPacked;
    private int priority; // Optional: 1 = high, 2 = medium, 3 = low
    private Timestamp createdAt;

    // --- Constructors ---
    public Item() {
        // Empty constructor for DAO use
    }

    public Item(String itemName, String category, boolean isPacked) {
        this.itemName = itemName;
        this.category = category;
        this.isPacked = isPacked;
    }

    public Item(int itemId, int listId, String itemName, String category, boolean isPacked, int priority, Timestamp createdAt) {
        this.itemId = itemId;
        this.listId = listId;
        this.itemName = itemName;
        this.category = category;
        this.isPacked = isPacked;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public void setPacked(boolean isPacked) {
        this.isPacked = isPacked;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // --- Utility ---
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
