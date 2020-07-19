package scripts.cooking_lumby_castle.actions;


import org.powerbot.script.Random;
import shared.constants.GameObjects;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import shared.tools.CommonActions;
import shared.tools.CommonAreas;

import java.util.concurrent.Callable;

public class WalkStairsToLumbyBank extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3205, 3209, 2), new Tile(3206, 3213, 2), new Tile(3208, 3217, 2)};
    private final int fishId;

    public WalkStairsToLumbyBank(ClientContext ctx, int fishId) {
        super(ctx, "To Bank");
        this.fishId = fishId;
    }

    @Override
    public boolean activate() {
        boolean noRawFish = ctx.inventory.select().id(fishId).count() == 0;
        return noRawFish && ctx.game.floor() == 2 && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        path[path.length - 1] = CommonAreas.lumbridgeCastleBank().getRandomTile();
        CommonActions.traversePath(ctx, path);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !ctx.players.local().inMotion() || ctx.bank.inViewport();
            }
        }, Random.nextInt(100, 500), 10);

    }
}
