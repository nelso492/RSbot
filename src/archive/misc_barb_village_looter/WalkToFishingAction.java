package ngc.misc_barb_village_looter;


import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToFishingAction extends BaseAction<ClientContext> {
    private static final Tile[] pathToFishing = {new Tile(3094, 3489, 0), new Tile(3090, 3490, 0), new Tile(3093, 3487, 0), new Tile(3097, 3484, 0), new Tile(3098, 3480, 0), new Tile(3098, 3476, 0), new Tile(3098, 3472, 0), new Tile(3098, 3468, 0), new Tile(3095, 3465, 0), new Tile(3091, 3465, 0), new Tile(3088, 3462, 0), new Tile(3088, 3458, 0), new Tile(3086, 3454, 0), new Tile(3090, 3451, 0), new Tile(3091, 3447, 0), new Tile(3094, 3443, 0), new Tile(3098, 3441, 0), new Tile(3101, 3438, 0), new Tile(3104, 3435, 0), new Tile(3107, 3439, 0)};
    private CommonAreas areas = new CommonAreas();
    private Area fishingArea = areas.getBarbFishingArea();
    private Area fishingAreaSm = areas.getBarbFishingAreaSm();

    public WalkToFishingAction(ClientContext ctx) {
        super(ctx, "Walk to Fishing");
    }

    @Override
    public boolean activate() {
        return !fishingArea.contains(ctx.players.local()) && !ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        // Add random tile to end of tile array to randomize ending location
        pathToFishing[pathToFishing.length -1] = fishingAreaSm.getRandomTile();
        ctx.movement.newTilePath(pathToFishing).traverse();
        sleep();
    }
}
