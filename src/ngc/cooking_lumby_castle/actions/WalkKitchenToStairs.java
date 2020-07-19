package ngc.cooking_lumby_castle.actions;


import ngc._resources.constants.GameObjects;
import ngc._resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class WalkKitchenToStairs extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3208, 3217, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};
    private int fishId;

    public WalkKitchenToStairs(ClientContext ctx, int _fishId) {
        super(ctx, "To Upstairs");
        fishId = _fishId;
    }

    @Override
    public boolean activate() {

        boolean noRawFish = ctx.inventory.select().id(fishId).count() == 0;
        return noRawFish && ctx.game.floor() == 0;
    }

    @Override
    public void execute() {
        GameObject staircase = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16671).nearest().poll();

        if( staircase.inViewport() ) {
            int floor = ctx.game.floor();
            staircase.interact("Climb-up");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() != floor;
                }
            }, 450, 8);
        } else {
            ctx.movement.step(staircase);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return staircase.inViewport();
                }
            }, 150, 4);
        }
    }
}
