package disabled.woodcutting_oak_varrock.actions;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;
import shared.tools.CommonAreas;

/**
 * [Description]
 */
public class WalkToGeBank extends StructuredAction {
    private Area geBank = CommonAreas.getGeAreaEast();
    public static final Tile[] path = {new Tile(3197, 3457, 0), new Tile(3197, 3461, 0), new Tile(3197, 3465, 0), new Tile(3197, 3469, 0), new Tile(3197, 3473, 0), new Tile(3194, 3476, 0), new Tile(3194, 3480, 0), new Tile(3192, 3484, 0), new Tile(3191, 3488, 0), new Tile(3187, 3488, 0), new Tile(3183, 3488, 0), new Tile(3179, 3487, 0), new Tile(3175, 3488, 0), new Tile(3171, 3488, 0)};

    public WalkToGeBank(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !geBank.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = geBank.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        AntibanTools.sleepDelay(AntibanTools.getRandomInRange(0, 3));
    }

    @Override
    public boolean isComplete() {
        return ctx.bank.inViewport();
    }
}
