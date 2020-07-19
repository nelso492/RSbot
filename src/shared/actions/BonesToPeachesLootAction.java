package shared.actions;

import shared.constants.Items;
import shared.models.BaseAction;
import shared.tools.AntibanTools;
import shared.tools.CommonActions;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.BasicQuery;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

/**
 * Special looting logic for bones when using B2P
 */
public class BonesToPeachesLootAction extends BaseAction<ClientContext> {
    private BasicQuery<GroundItem> groundItems;
    private int[] boneIds;

    public BonesToPeachesLootAction(ClientContext ctx, String status) {
        super(ctx, status);
        this.boneIds = new int[]{Items.BONES_526, Items.BIG_BONES_532};
    }

    public BonesToPeachesLootAction(ClientContext ctx, String status, int[] boneIds) {
        super(ctx, status);
        this.boneIds = boneIds;
    }

    @Override
    public boolean activate() {
        groundItems = ctx.groundItems.select().select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem groundItem) {
                return groundItem.inViewport() && (groundItem.id() == boneIds[0] || groundItem.id() == boneIds[1]);
            }
        });

        return !groundItems.isEmpty() && ctx.inventory.count() <= 27 && ctx.inventory.select().id(boneIds).count() + ctx.inventory.select().id(CommonActions.allFoodIds()).count() < 8;
    }

    @Override
    public void execute() {
        GroundItem item = groundItems.nearest().poll();
        if ((!ctx.inventory.isFull() || item.stackable()) && item.valid()) {
            // Pause for human nature
            AntibanTools.sleepDelay(2);

            if (item.valid()) {
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

    public BasicQuery<GroundItem> getGroundItems() {
        return groundItems;
    }

    public void setGroundItems(BasicQuery<GroundItem> groundItems) {
        this.groundItems = groundItems;
    }

    public int[] getBoneIds() {
        return boneIds;
    }

    public void setBoneIds(int[] boneIds) {
        this.boneIds = boneIds;
    }
}
