package shared.actions;

import shared.models.BaseAction;
import shared.tools.CommonActions;
import shared.models.LootItem;
import shared.models.LootList;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Looting based on custom Loot Item objects.
 */
public class LootAction extends BaseAction<ClientContext> {
    private LootList lootList;
    private boolean lootDuringCombat;
    private int maxDistanceToLoot;

    public LootAction(ClientContext ctx, String status, LootList lootList, int maxDistanceToLoot, boolean lootDuringCombat) {
        super(ctx, status);
        this.lootList = lootList;
        this.maxDistanceToLoot = maxDistanceToLoot;
        this.lootDuringCombat = lootDuringCombat;
    }

    @Override
    public boolean activate() {
        // Item can be picked up, stack is above min size, item in viewport
        // Ground Item not in our loot list
        BasicQuery<GroundItem> groundItems = ctx.groundItems.select().id(this.lootList.allItemIds()).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                LootItem i = lootList.getLootItemById(groundItem.id());
                if (i != null) {
                    // Item can be picked up, stack is above min size, item in viewport
                    return CommonActions.isValidLoot(ctx, groundItem, i, maxDistanceToLoot);
                } else {
                    // Ground Item not in our loot list
                    return false;
                }
            }
        });

        return !groundItems.isEmpty() && (this.lootDuringCombat || !ctx.players.local().interacting().valid());
    }

    @Override
    public void execute() {
        for (GroundItem item : ctx.groundItems) {
            if (!ctx.inventory.isFull() || (item.stackable() && ctx.inventory.select().id(item.id()).count() == 1)) {
                // Pause for human nature
                item.interact("Take", item.name());
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid();
                    }
                }, Random.nextInt(50, 150), 20);

                if (item.valid() && item.tile().distanceTo(ctx.players.local()) > 1) {
                    sleep(400);
                }
            }
        }
    }

    public LootList getLootList() {
        return lootList;
    }

    public void setLootList(LootList lootList) {
        this.lootList = lootList;
    }

    public boolean isLootDuringCombat() {
        return lootDuringCombat;
    }

    public void setLootDuringCombat(boolean lootDuringCombat) {
        this.lootDuringCombat = lootDuringCombat;
    }

    public int getMaxDistanceToLoot() {
        return maxDistanceToLoot;
    }

    public void setMaxDistanceToLoot(int maxDistanceToLoot) {
        this.maxDistanceToLoot = maxDistanceToLoot;
    }
}
