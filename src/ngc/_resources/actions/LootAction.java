package ngc._resources.actions;

import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.models.LootItem;
import ngc._resources.models.LootList;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class LootAction extends BaseAction<ClientContext> {
    private LootList lootList;
    private boolean lootDuringCombat;
    private int maxDistanceToLoot;
    private BasicQuery<GroundItem> groundItems;
    private boolean ignoreB2P;

    public LootAction(ClientContext ctx, String status, LootList lootList, int maxDistanceToLoot, boolean lootDuringCombat, boolean ignoreB2P) {
        super(ctx, status);
        this.lootList = lootList;
        this.maxDistanceToLoot = maxDistanceToLoot;
        this.lootDuringCombat = lootDuringCombat;
        this.ignoreB2P = ignoreB2P;
    }

    @Override
    public boolean activate() {
        groundItems = ctx.groundItems.select().id(this.lootList.allItemIds()).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                LootItem i = lootList.getLootItemById(groundItem.id());
                if( i != null ) {
                    // Item can be picked up, stack is above min size, item in viewport
                    return CommonFunctions.isValidLoot(ctx, groundItem, i, maxDistanceToLoot, ignoreB2P);
                } else {
                    // Ground Item not in our loot list
                    return false;
                }
            }
        });

        return !groundItems.isEmpty();
    }

    @Override
    public void execute() {
        for( GroundItem item : ctx.groundItems ) {
            if( !ctx.inventory.isFull() || (item.stackable() && ctx.inventory.select().id(item.id()).count() == 1) ) {
                // Pause for human nature
                item.interact("Take", item.name());
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid();
                    }
                }, Random.nextInt(50, 150), 20);

                if( item.valid() && item.tile().distanceTo(ctx.players.local()) > 1){
                    sleep(400);
                }
            }
        }
    }
}