/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author cindy
 */
class PackingProgress {
    private int totalItems;
    private int packedItems;
    
    public PackingProgress(int totalItems, int PackedItems){
        this.totalItems = totalItems;
        this.packedItems = packedItems;
    }
    
    public int getTotalItems(){
        return totalItems;
    }
    
    public void setTotalItems(int totalItems){
        this.totalItems = totalItems;
    }
    
    public int getPackedItems(int PackedItems){
        return packedItems;
    }
    
    public void  setPackedItems(int packedItems){
        this.packedItems = packedItems;
    }
    
    public double getCompletionPercentage(){
        if(totalItems == 0) return 0.0;
        return((double) packedItems / totalItems) *100.0;
    }
    
    
    @Override
    public String toString(){
        return String.format("Packed %d of %d items (%.2f%%)", packedItems, totalItems, getCompletionPercentage());
    }
    
}
