package ngc.wc_oak_cutter;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBankAction extends BaseAction<ClientContext> {
    private Area geBank = CommonAreas.getGeAreaEast();
    public static final Tile[] path = {new Tile(3197, 3457, 0), new Tile(3197, 3461, 0), new Tile(3197, 3465, 0), new Tile(3197, 3469, 0), new Tile(3197, 3473, 0), new Tile(3194, 3476, 0), new Tile(3194, 3480, 0), new Tile(3192, 3484, 0), new Tile(3191, 3488, 0), new Tile(3187, 3488, 0), new Tile(3183, 3488, 0), new Tile(3179, 3487, 0), new Tile(3175, 3488, 0), new Tile(3171, 3488, 0)};
    public WalkToBankAction(ClientContext ctx) {
        super(ctx, "To Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !geBank.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = geBank.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
