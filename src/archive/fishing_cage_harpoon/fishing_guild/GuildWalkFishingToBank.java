package scripts.fishing_cage_harpoon.fishing_guild;

import shared.models.BaseAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class GuildWalkFishingToBank extends BaseAction<ClientContext> {

    private Tile[] path;
    private Area bankArea;

    public GuildWalkFishingToBank(ClientContext ctx) {
        super(ctx, "Dock to Bank");
        this.bankArea = CommonAreas.fishingGuildBank();
        this.path = new Tile[] {new Tile(2596, 3420, 0), new Tile(2594, 3416, 0), new Tile(2590, 3416, 0)};

    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        if( ctx.players.local().tile().y() >= 3420 ) {
            this.path[0] = new Tile(2596, 3420, 0);
        } else {
            this.path[0] = new Tile(2601, 3406, 0);
        }

        path[path.length - 1] = bankArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
