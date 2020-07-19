package scripts.cooking_lumby_castle.actions;


import shared.constants.GameObjects;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class NavigateStairs extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3208, 3217, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};
    private int rawId;
    private int[] allStairs = {GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16671, GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16672, GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673};

    public NavigateStairs(ClientContext ctx, int _rawId) {
        super(ctx, "Climbing Stairs");
        rawId = _rawId;
    }

    @Override
    public boolean activate() {
        GameObject stairs = ctx.objects.select().id(allStairs).nearest().poll();
        int rawFishCount = ctx.inventory.select().id(rawId).count();
        return stairs.inViewport() &&
                stairs.tile().distanceTo(ctx.players.local()) < 4  &&
                !ctx.players.local().interacting().valid() &&
                ctx.players.local().animation() == -1;
    }

    @Override
    public void execute() {
        int rawFishCount = ctx.inventory.select().id(rawId).count();
        int currentFloor = ctx.game.floor();
        int waitTime = Random.nextInt(150, 450);

        if( rawFishCount != 0 && currentFloor == 2 ) {
            ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).nearest().poll().interact("Climb-down");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 1;
                }
            }, waitTime, 5);
        }

        if( rawFishCount != 0 && currentFloor == 1 ) {
            ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16672).nearest().poll().interact("Climb-down");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 0;
                }
            }, waitTime, 5);
        }
        if( rawFishCount == 0 && currentFloor == 0 ) {
            ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16671).nearest().poll().interact("Climb-up");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 1;
                }
            }, waitTime, 5);
        }

        if( rawFishCount == 0 && currentFloor == 1 ) {
            ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16672).nearest().poll().interact("Climb-up");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 2;
                }
            }, waitTime, 5);
        }
    }
}
