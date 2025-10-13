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

    public Item(int listId, String itemName, String category, boolean isPacked, int priority, Timestamp createdAt) {
        this.listId = listId;
        this.itemName = itemName;
        this.category = category;
        this.isPacked = isPacked;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public Item() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getListId() { return listId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public boolean isPacked() { return isPacked; }
    public int getPriority() { return priority; }
    public Timestamp getCreatedAt() { return createdAt; }

    public int getItemId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setListId(int listId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setItemName(String itemName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setCategory(String trim) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setPacked(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
