package scripts.cooking_lumby_castle.actions;


import org.powerbot.script.Random;
import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.templates.StructuredAction;
import shared.tools.CommonActions;
import shared.tools.CommonAreas;

import java.util.concurrent.Callable;

public class WalkLumbyBankToStairs extends StructuredAction {
    public static final Tile[] path = {new Tile(3207, 3220, 2), new Tile(3206, 3219, 2), new Tile(3208, 3216, 2), new Tile(3206, 3213, 2), new Tile(3205, 3211, 2), new Tile(3205, 3208, 2)};
    private final int fishId;

    public WalkLumbyBankToStairs(ClientContext ctx, int fishId) {
        super(ctx, "BankToStairs");
        this.fishId = fishId;
    }

    @Override
    public boolean activate() {
        GameObject staircase = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).poll();

        boolean hasRawFish = ctx.inventory.select().id(fishId).count() != 0;
        return hasRawFish && ctx.game.floor() == 2;
    }

    @Override
    public void execute() {
        GameObject staircase = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).nearest().poll();

        if (staircase.valid() && !staircase.inViewport() && !CommonAreas.lumbridgeCastleBank().contains(ctx.players.local())) {
            ctx.camera.turnTo(staircase);
        }

        ctx.movement.newTilePath(path).traverse();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.players.local().inMotion();
            }
        }, Random.nextInt(100, 500), 10);

    }

    @Override
    public boolean isComplete() {
        GameObject staircase = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).nearest().poll();

        return staircase.inViewport() && ctx.inventory.select().id(this.fishId).count() > 0 && ctx.players.local().tile().y() < 3212;
    }
}
