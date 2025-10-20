package models;

import java.sql.Date;

public class PackingList {
    private int listId;
    private int userId;
    private String listName;
    private String description;
    private String destination;
    private Date startDate;
    private Date endDate;
    private String tripType;
    private String subtitle;
    private int totalItems;
    private int packedItems;

    // Getters and Setters
    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public int getPackedItems() { return packedItems; }
    public void setPackedItemsCount(int packedItems) { this.packedItems = packedItems; }
    
    @Override
    public String toString() {
        return "PackingList{" +
                "listId=" + listId +
                ", listName='" + listName + '\'' +
                ", destination='" + destination + '\'' +
                ", tripType='" + tripType + '\'' +
                '}';
    }
}