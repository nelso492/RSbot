package ngc.combat_alkharid_warriors;

import resources.models.BaseAction;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GroundItem;

import static org.powerbot.script.Condition.sleep;

public class LootArrowsAction extends BaseAction<ClientContext> {
    private final int IRON_ARROW_ID = 884;

    public LootArrowsAction(ClientContext ctx) {
        super(ctx, "Looting");
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotMoving = !ctx.players.local().inMotion();
        boolean interacting = ctx.players.local().interacting().valid();
        boolean arrowStackNearby = ctx.groundItems.select().id(IRON_ARROW_ID).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem arr) {
                return arr.stackSize() >= 2 && arr.inViewport();
            }
        }).poll().valid();
        return !interacting && playerNotMoving && playerNotAnimated && arrowStackNearby;
    }

    @Override
    public void execute() {
        //get your bearings, find those arrows, and get em
        sleep();
        if( ctx.inventory.select().id(IRON_ARROW_ID).poll().stackSize() > 50 ) {
            ctx.inventory.select().poll().click();
        }

        ctx.groundItems.select().id(IRON_ARROW_ID).select(new Filter<GroundItem>() {
            @Override
            public boolean accept(GroundItem arr) {
                return arr.stackSize() >= 2;
            }
        }).poll().interact("Take", "Iron Arrow");
        sleep();
    }
}
