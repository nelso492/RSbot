package scripts.wc_oak_cutter;


import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToOakTreeAction extends AbstractAction<ClientContext> {
    private Area oakArea = CommonAreas.getVarrockOakTrees();
    public static final Tile[] path = {new Tile(3168, 3489, 0), new Tile(3172, 3489, 0), new Tile(3176, 3489, 0), new Tile(3180, 3489, 0), new Tile(3184, 3489, 0), new Tile(3188, 3489, 0), new Tile(3192, 3488, 0), new Tile(3192, 3484, 0), new Tile(3195, 3481, 0), new Tile(3194, 3477, 0), new Tile(3194, 3473, 0), new Tile(3194, 3469, 0), new Tile(3195, 3465, 0), new Tile(3195, 3461, 0)};

    public WalkToOakTreeAction(ClientContext ctx) {
        super(ctx, "To Oaks");
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        path[path.length - 1] = oakArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
