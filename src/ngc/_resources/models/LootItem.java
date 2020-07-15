package ngc._resources.models;

public class LootItem {
    private int itemId;
    private int minStackSize;
    private int maxInventoryCount;

    public LootItem(int itemId) {
        this.itemId = itemId;
        this.minStackSize = 1;
        this.maxInventoryCount = -1;
    }

    public LootItem(int itemId, int minStackSize) {
        this.itemId = itemId;
        this.minStackSize = minStackSize;
        this.maxInventoryCount = -1;
    }

    public LootItem(int itemId, int minStackSize, int maxInventoryCount) {
        this.itemId = itemId;
        this.minStackSize = minStackSize;
        this.maxInventoryCount = maxInventoryCount;
    }

    public int getItemId() {
        return itemId;
    }

    public int getMinStackSize() {
        return minStackSize;
    }

    public int getMaxInventoryCount() {
        return maxInventoryCount;
    }
}
