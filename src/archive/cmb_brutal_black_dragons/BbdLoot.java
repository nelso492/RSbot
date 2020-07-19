package ngc.cmb_brutal_black_dragons;

import ngc._resources.models.BaseAction;
import ngc._resources.models.LootList;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;

import java.util.concurrent.Callable;

public class BbdLoot extends BaseAction<ClientContext> {
    private int[] lootIds;
    private LootList lootList;

    public BbdLoot(ClientContext ctx, String status, LootList lootList) {
        super(ctx, status);
        this.lootList = lootList;
        this.lootIds = lootList.allItemIds();
    }

    @Override
    public boolean activate() {

        return ctx.groundItems.select().id(this.lootIds).nearest().peek().valid() && ctx.groundItems.select().id(this.lootIds).nearest().peek().tile().distanceTo(ctx.players.local()) < 15 &&
                !ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        GroundItem item = ctx.groundItems.poll();

        if( item.valid() ) {
            if( item.inViewport() ) {
                if( item.tile().distanceTo(ctx.players.local()) > 0 ) {
                    item.interact("Take", item.name());
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return item.tile().distanceTo(ctx.players.local()) == 0;
                        }
                    }, 100, 25);
                } else {

                    // bypass non loot stacks
                    if( ctx.groundItems.select(new Filter<GroundItem>() {
                        @Override
                        public boolean accept(GroundItem groundItem) {
                            return lootList.getLootItemById(groundItem.id()) == null && groundItem.tile().distanceTo(item.tile()) == 0;
                        }
                    }).poll().valid() ) {
                        // Coin stack found
                        item.interact("Take", item.name());
                    } else {
                        item.interact("Take");
                    }
                }
            } else {
                ctx.movement.step(item);

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().inMotion();
                    }
                }, 100, 20);

            }
        }
    }
}
