package models;

public class PackingProgress {
    private int totalItems;
    private int packedItems;

    public PackingProgress(int totalItems, int packedItems) {
        this.totalItems = totalItems;
        this.packedItems = packedItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getPackedItems() {
        return packedItems;
    }

    public void setPackedItems(int packedItems) {
        this.packedItems = packedItems;
    }

    // Calculate completion percentage safely
    public double getCompletionPercentage() {
        if (totalItems == 0) return 0.0;
        return ((double) packedItems / totalItems) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("Packed %d of %d items (%.2f%%)", 
            packedItems, totalItems, getCompletionPercentage());
    }
}
