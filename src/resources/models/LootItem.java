package resources.models;

/**
 * Standard parameters for managing loot items.
 */
public class LootItem {
    private final int itemId;
    private final int minStackSize;
    private final int maxInventoryCount;

    /**
     * Initialize new loot item by id with no min stack size or max inv count
     * @param itemId OSRS item id
     */
    public LootItem(int itemId) {
        this.itemId = itemId;
        this.minStackSize = 1;
        this.maxInventoryCount = -1;
    }

    /**
     * New list item with ID and min stack size for pickup
     * @param itemId OSRS item id
     * @param minStackSize min stack size for validation. i.e. only arrows of 5 stack or more
     */
    public LootItem(int itemId, int minStackSize) {
        this.itemId = itemId;
        this.minStackSize = minStackSize;
        this.maxInventoryCount = -1;
    }

    /**
     * New Loot item with stack size and max count requirements.
     * Remember stackable items always have a count of 1.
     * @param itemId
     * @param minStackSize
     * @param maxInventoryCount
     */
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
