package models;

import java.sql.Date;
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
    private List<Item> items; // optional — if you want to hold related items

    // 🟩 Full constructor
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

    // 🟩 Constructor for new list (before inserting to DB)
    public PackingList(int userId, String listName, String description,
                       String destination, Date startDate, Date endDate, String tripType) {
        this.userId = userId;
        this.listName = listName;
        this.description = description;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tripType = tripType;
    }

    // 🟩 Empty constructor
    public PackingList() {}

    // ===========================
    // Getters
    // ===========================
    public int getListId() { return listId; }
    public int getUserId() { return userId; }
    public String getListName() { return listName; }
    public String getDescription() { return description; }
    public String getDestination() { return destination; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getTripType() { return tripType; }
    public List<Item> getItems() { return items; }

    // ===========================
    // Setters
    // ===========================
    public void setListId(int listId) { this.listId = listId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setListName(String listName) { this.listName = listName; }
    public void setDescription(String description) { this.description = description; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public void setTripType(String tripType) { this.tripType = tripType; }
    public void setItems(List<Item> items) { this.items = items; }

    // ===========================
    // Utility Methods
    // ===========================
    public int getPackedItemsCount() {
        if (items == null || items.isEmpty()) return 0;
        return (int) items.stream().filter(Item::isPacked).count();
    }

    public int getTotalItemsCount() {
        return (items == null) ? 0 : items.size();
    }

    public int getPackingPercentage() {
        int total = getTotalItemsCount();
        if (total == 0) return 0;
        return (int) ((getPackedItemsCount() / (double) total) * 100);
    }

    @Override
    public String toString() {
        return "PackingList{" +
                "listId=" + listId +
                ", userId=" + userId +
                ", listName='" + listName + '\'' +
                ", destination='" + destination + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tripType='" + tripType + '\'' +
                '}';
    }

    public String getTotalItems() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}


