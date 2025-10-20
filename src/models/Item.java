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

    public Item() {
        // Default constructor
    }

    public Item(int listId, String itemName, String category, boolean packed, int priority, Timestamp createdAt) {
        this.listId = listId;
        this.itemName = itemName;
        this.category = category;
        this.packed = packed;
        this.priority = priority;
        this.createdAt = createdAt;
    }


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
        return packed;
    }

    public void setPacked(boolean packed) {
        this.packed = packed;
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

