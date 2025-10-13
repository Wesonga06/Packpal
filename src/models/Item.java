package models;

import java.sql.Timestamp;

public class Item {
    private int itemId;
    private int listId;
    private String itemName;
    private String category;
    private boolean packed;
    private int priority;
    private Timestamp createdAt;

    // Full constructor
    public Item(int itemId, int listId, String itemName, String category,
                boolean packed, int priority, Timestamp createdAt) {
        this.itemId = itemId;
        this.listId = listId;
        this.itemName = itemName;
        this.category = category;
        this.packed = packed;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    // Constructor for new item before inserting to DB
    public Item(int listId, String itemName, String category, boolean packed,
                int priority, Timestamp createdAt) {
        this.listId = listId;
        this.itemName = itemName;
        this.category = category;
        this.packed = packed;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    // Empty constructor
    public Item() {}

    // ===========================
    // Getters
    // ===========================
    public int getItemId() {
        return itemId;
    }

    public int getListId() {
        return listId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public boolean isPacked() {
        return packed;
    }

    public int getPriority() {
        return priority;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // ===========================
    // Setters
    // ===========================
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPacked(boolean packed) {
        this.packed = packed;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

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
                ", packed=" + packed +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}

