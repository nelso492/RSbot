package ngc.runecrafter;


import resources.models.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkFromRuins extends BaseAction<ClientContext> {

    private Tile[] path;
    private int ruinsYCoord;
    private Area bankArea;

    public WalkFromRuins(ClientContext ctx, Tile[] path, Area bankArea, int ruinsYCoord) {
        super(ctx, "To Bank");
        this.path = path;
        this.ruinsYCoord = ruinsYCoord;
        this.bankArea = bankArea;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.count() == 1 && ctx.players.local().tile().y() < ruinsYCoord && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        path[path.length -1] = bankArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
