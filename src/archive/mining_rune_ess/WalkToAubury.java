package ngc.mining_rune_ess;


import resources.models.BaseAction;
import resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToAubury extends BaseAction<ClientContext> {
    private CommonAreas areas = new CommonAreas();
    private Area runeShop = areas.getAuburyRuneShop();
    private Area essMinerBounds = areas.getVarrockEssMinerBounds();
    public static final Tile[] path = {new Tile(3253, 3420, 0), new Tile(3253, 3424, 0), new Tile(3256, 3427, 0), new Tile(3260, 3428, 0), new Tile(3260, 3424, 0), new Tile(3260, 3420, 0), new Tile(3260, 3416, 0), new Tile(3260, 3412, 0), new Tile(3260, 3408, 0), new Tile(3259, 3404, 0), new Tile(3257, 3400, 0), new Tile(3253, 3398, 0)};    public WalkToAubury(ClientContext ctx) {
        super(ctx, "To Aubury");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isEmpty() && !runeShop.contains(ctx.players.local()) && essMinerBounds.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = runeShop.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
