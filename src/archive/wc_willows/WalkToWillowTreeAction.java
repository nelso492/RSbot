package ngc.wc_willows;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToWillowTreeAction extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3046, 3235, 0), new Tile(3042, 3237, 0), new Tile(3042, 3241, 0), new Tile(3042, 3245, 0), new Tile(3046, 3246, 0), new Tile(3050, 3246, 0), new Tile(3053, 3249, 0), new Tile(3056, 3253, 0), new Tile(3060, 3254, 0)};
    private Area willowArea;

    public WalkToWillowTreeAction(ClientContext ctx) {
        super(ctx, "To Willows");
        this.willowArea = CommonAreas.getPortSarimWillows();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() < 28 && !willowArea.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = willowArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
