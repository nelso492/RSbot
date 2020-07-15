package ngc.fishing_net_draynor;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBankAction extends BaseAction<ClientContext> {
    private CommonAreas areas = new CommonAreas();
    private Area draynorBank = areas.getDraynorBank();
    public static final Tile[] path = {new Tile(3088, 3226, 0), new Tile(3090, 3230, 0), new Tile(3090, 3234, 0), new Tile(3089, 3238, 0), new Tile(3087, 3242, 0), new Tile(3087, 3246, 0), new Tile(3091, 3247, 0), new Tile(3092, 3243, 0)};

    public WalkToBankAction(ClientContext ctx) {
        super(ctx, "Walk to Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 28 && !draynorBank.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = draynorBank.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
