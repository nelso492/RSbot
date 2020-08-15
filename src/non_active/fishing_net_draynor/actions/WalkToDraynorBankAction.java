package scripts.fishing_net_draynor.actions;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.CommonAreas;

public class WalkToDraynorBankAction extends StructuredAction {

    private final Tile[] path = {new Tile(3087, 3227, 0), new Tile(3089, 3231, 0), new Tile(3090, 3235, 0), new Tile(3087, 3239, 0), new Tile(3087, 3243, 0), new Tile(3087, 3247, 0), new Tile(3091, 3247, 0), new Tile(3092, 3243, 0)};

    public WalkToDraynorBankAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean isComplete() {
        return ctx.bank.inViewport() && CommonAreas.getDraynorBank().contains(ctx.players.local()) && !ctx.players.local().inMotion();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !CommonAreas.getDraynorBank().contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = CommonAreas.getDraynorBank().getRandomTile();
        ctx.movement.newTilePath(path).traverse();
    }
}
