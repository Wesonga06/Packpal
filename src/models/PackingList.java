package models;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class PackingList {
    private int listId;
    private int userId;
    private String listName;
    private String description;
    private String destination;
    private Date startDate;
    private Date endDate;
    private String tripType;
    private Timestamp createdAt;
    private List<Item> items;

    // --- Constructors ---
    public PackingList() {}

    public PackingList(int listId, int userId, String listName, String description,
                       String destination, Date startDate, Date endDate, String tripType) {
        this.listId = listId;
        this.userId = userId;
        this.listName = listName;
        this.description = description;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tripType = tripType;
    }

    // --- Getters ---
    public int getListId() {
        return listId;
    }

    public int getUserId() {
        return userId;
    }

    public String getListName() {
        return listName;
    }

    public String getDescription() {
        return description;
    }

    public String getDestination() {
        return destination;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTripType() {
        return tripType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public List<Item> getItems() {
        return items;
    }

    // --- Setters ---
    public void setListId(int listId) {
        this.listId = listId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // --- Utility methods ---
    public int getPackedItemsCount() {
        if (items == null) return 0;
        return (int) items.stream().filter(Item::isPacked).count();
    }

    public int getPackingPercentage() {
        if (items == null || items.isEmpty()) return 0;
        return (int) ((getPackedItemsCount() / (double) items.size()) * 100);
    }

    public int getTotalItems() {
        return items == null ? 0 : items.size();
    }

    @Override
    public String toString() {
        return listName + " (" + destination + ")";
    }
}
