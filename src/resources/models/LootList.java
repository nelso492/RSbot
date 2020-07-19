package resources.models;

import java.util.ArrayList;

/**
 * List actions for loot items
 */
public class LootList {
    private ArrayList<LootItem> lootItems;

    public LootList() {
        this.lootItems = new ArrayList<>();
    }

    public ArrayList<LootItem> getLootItems() {
        return lootItems;
    }

    public LootItem getLootItemById(int id) {
        for( LootItem i : this.lootItems ) {
            if( i.getItemId() == id ) {
                return i;
            }
        }

        return null;
    }

    public void setLootItems(ArrayList<LootItem> lootItems) {
        this.lootItems = lootItems;
    }

    public void addLootItem(LootItem item) {
        this.lootItems.add(item);
    }

    public int[] allItemIds() {
        int[] ids = new int[this.lootItems.size()];

        for( int i = 0; i < ids.length; i++ ) {
            ids[i] = this.lootItems.get(i).getItemId();
        }

        return ids;
    }
}
