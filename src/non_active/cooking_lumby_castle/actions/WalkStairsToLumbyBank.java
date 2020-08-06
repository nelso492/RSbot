package scripts.cooking_lumby_castle.actions;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.CommonAreas;

public class WalkStairsToLumbyBank extends StructuredAction {
    public static final Tile[] path = {new Tile(3205, 3209, 2), new Tile(3206, 3213, 2), new Tile(3206, 3216, 2), new Tile(0, 0, 2)};
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
        ctx.movement.newTilePath(path).traverse();
    }

    @Override
    public boolean isComplete() {
        return CommonAreas.lumbridgeCastleBank().contains(ctx.players.local()) || ctx.bank.inViewport();
    }
}
