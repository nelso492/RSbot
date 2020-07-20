package scripts.mining_rune_ess;


import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBank extends AbstractAction<ClientContext> {
    private CommonAreas areas = new CommonAreas();
    private Area eastBank = areas.getVarrockBankEast();
    private Area essMinerBounds = areas.getVarrockEssMinerBounds();
    public static final Tile[] path = {new Tile(3253, 3401, 0), new Tile(3256, 3404, 0), new Tile(3256, 3408, 0), new Tile(3259, 3412, 0), new Tile(3260, 3416, 0), new Tile(3260, 3420, 0), new Tile(3260, 3424, 0), new Tile(3259, 3428, 0), new Tile(3255, 3427, 0), new Tile(3254, 3423, 0)};

    public WalkToBank(ClientContext ctx) {
        super(ctx, "To Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !eastBank.contains(ctx.players.local()) && essMinerBounds.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = eastBank.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
