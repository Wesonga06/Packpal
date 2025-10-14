package models;

import java.sql.Date;

public class PackingList {
    private int id;
    private String listName;
    private String description;
    private Date startDate;
    private Date endDate;
    private String destination;
    private String tripType;
    private int userId;
    private int totalItems;
    private int listId;
    private int packedItemsCount;

    // ✅ Constructors
    public PackingList() {}

    public PackingList(int id, String listName, String description, Date startDate, Date endDate, String tripType, int userId) {
        this.id = id;
        this.listName = listName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tripType = tripType;
        this.userId = userId;
    }

    // ✅ Getters
    public int getId() {
        return id;
    }

    public String getListName() {
        return listName;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public int getListId(){
        return listId;
    }

    public Date getEndDate() {
        return endDate;
    }
    
    public String destination() {
    return destination;
}

    public String getTripType() {
        return tripType;
    }

    public int getUserId() {
        return userId;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getPackedItemsCount() {
        return packedItemsCount;
    }
    
    public int getPackingPercentage(){
        if(totalItems == 0) return 0;
        return (int) ((packedItemsCount * 100.0) / totalItems);
    }
    
    public String getDestination(){
        return destination;
    }

    // ✅ Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    // 👇 Add these newly requested methods
    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public void setDestination(String destination){
        this.destination = destination;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setListId(int listId){
        this.listId = listId;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public void setPackedItemsCount(int packedItemsCount) {
        this.packedItemsCount = packedItemsCount;
    }
}

