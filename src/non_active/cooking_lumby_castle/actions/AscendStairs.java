package scripts.cooking_lumby_castle.actions;


import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.constants.GameObjects;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;

import java.util.concurrent.Callable;

public class AscendStairs extends StructuredAction {
    public static final Tile[] path = {new Tile(3208, 3217, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};
    private int[] allStairs = {GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16671, GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16672};

    public AscendStairs(ClientContext ctx) {
        super(ctx, "Climbing Stairs");
    }

    @Override
    public boolean activate() {
        GameObject stairs = ctx.objects.select().id(allStairs).nearest().poll();
        return stairs.inViewport() &&
                stairs.tile().distanceTo(ctx.players.local()) < 4 &&
                !ctx.players.local().interacting().valid() &&
                ctx.players.local().animation() == -1;
    }

    @Override
    public void execute() {
        int currentFloor = ctx.game.floor();
        int waitTime = AntibanTools.getRandomInRange(150, 450);

        if (currentFloor == 0) {
            ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16671).nearest().poll().interact("Climb-up");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 1;
                }
            }, waitTime, 5);
        }

        if (currentFloor == 1) {
            ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16672).nearest().poll().interact("Climb-up");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 2;
                }
            }, waitTime, 5);
        }
    }

    @Override
    public boolean isComplete() {
        return ctx.game.floor() == 2;
    }
}
