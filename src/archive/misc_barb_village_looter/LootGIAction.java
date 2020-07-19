package ngc.misc_barb_village_looter;

import ngc._resources.models.BaseAction;
import ngc._resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;

import java.util.concurrent.Callable;

public class LootGIAction extends BaseAction<ClientContext> {
    private CommonAreas areas = new CommonAreas();

    private final int[] LOOT_IDS = {329, 331, 333, 335, 351};
    private Area fishingArea = areas.getBarbFishingArea();


    public LootGIAction(ClientContext ctx) {
        super(ctx, "Looting");
    }

    @Override
    public boolean activate() {
        boolean inventoryNotFull = !ctx.inventory.isFull();
        boolean inArea = fishingArea.contains(ctx.players.local());
        return inventoryNotFull && inArea && !ctx.players.local().inMotion();
    }

    @Override
    public void execute() {
        //get your bearings, find those arrows, and get em
        GroundItem loot = ctx.groundItems.select().id(LOOT_IDS).nearest().poll();

        if( loot.inViewport() ) {
            int invCount = ctx.inventory.select().count();

            loot.interact("Take");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().count() > invCount;
                }
            }, Random.nextInt(50, 150), 5);
        }
    }
}
