package scripts.cooking_lumby_castle.actions;


import org.powerbot.script.Random;
import shared.constants.GameObjects;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.tools.CommonActions;

import java.util.concurrent.Callable;

public class WalkLumbyBankToStairs extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3206, 3219, 2), new Tile(3208, 3216, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};
    private final int fishId;

    public WalkLumbyBankToStairs(ClientContext ctx, int fishId) {
        super(ctx, "To Stairs");
        this.fishId = fishId;
    }

    @Override
    public boolean activate() {
        GameObject staircase = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).poll();

        boolean hasRawFish = ctx.inventory.select().id(fishId).count() != 0;
        return hasRawFish && ctx.game.floor() == 2 && (!staircase.valid() || !staircase.inViewport());
    }

    @Override
    public void execute() {
        GameObject staircase = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).nearest().poll();

        if (staircase.inViewport() && staircase.tile().y() < 3220) {
            int floor = ctx.game.floor();
            staircase.interact("Climb-down");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() != floor;
                }
            }, 450, 8);
        } else {
            CommonActions.traversePath(ctx, path);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion() || staircase.inViewport();
                }
            }, Random.nextInt(100, 500), 10);
        }
    }
}
