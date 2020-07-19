package ngc.fishing_cage_harpoon.fishing_guild;

import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class GuildWalkBankToFishing extends BaseAction<ClientContext> {

    private Tile[] path;
    private int fishingSpotId;
    private Area fishingDock;

    public GuildWalkBankToFishing(ClientContext ctx, int fishingSpotId) {
        super(ctx, "Bank To Dock");
        this.path = new Tile[] {new Tile(2588, 3418, 0), new Tile(2592, 3415, 0), new Tile(2593, 3419, 0), new Tile(2597, 3420, 0)};
        this.fishingSpotId = fishingSpotId;
        this.fishingDock = new Area(new Tile(2592, 3421, 0), new Tile(2595, 3412, 0));
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && !ctx.players.local().interacting().valid() && CommonAreas.fishingGuildBank().contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = fishingDock.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
