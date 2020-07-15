package ngc._resources.actions;

import ngc._resources.Items;
import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonFunctions;
import ngc._resources.functions.GaussianTools;
import ngc._resources.models.LootList;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class BonesToPeachesLootAction extends BaseAction<ClientContext> {
    private LootList lootList;
    private BasicQuery<GroundItem> groundItems;
    private int[] boneIds;

    public BonesToPeachesLootAction(ClientContext ctx, String status) {
        super(ctx, status);
        this.boneIds = new int[] {Items.BONES_526, Items.BIG_BONES_532};
    }

    @Override
    public boolean activate() {
        groundItems = ctx.groundItems.select().select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                return groundItem.inViewport() && (groundItem.id() == boneIds[0] || groundItem.id() == boneIds[1]);
            }
        });

        return !groundItems.isEmpty() && ctx.inventory.count() <= 27 && ctx.inventory.select().id(boneIds).count() + ctx.inventory.select().id(CommonFunctions.allFoodIds()).count() < 8;
    }

    @Override
    public void execute() {
        GroundItem item = groundItems.nearest().poll();
        if( (!ctx.inventory.isFull() || item.stackable()) && item.valid() ) {
            // Pause for human nature
            sleep(GaussianTools.getRandomGaussian(600, 200));

            if( item.valid() ) {
                item.interact("Take", item.name());

                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !item.valid();
                    }
                }, Random.nextInt(150, 250), 20);
            }
        }
    }
}