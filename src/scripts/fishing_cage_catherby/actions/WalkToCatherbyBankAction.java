package scripts.fishing_cage_catherby.actions;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.CommonAreas;

public class WalkToCatherbyBankAction extends StructuredAction {

    private final Tile[] path = {new Tile(2849, 3433, 0), new Tile(2843, 3435, 0), new Tile(2838, 3435, 0), new Tile(2831, 3437, 0), new Tile(2825, 3439, 0), new Tile(2818, 3437, 0), new Tile(2813, 3437, 0), new Tile(2809, 3438, 0)};

    public WalkToCatherbyBankAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean isComplete() {
        return ctx.bank.inViewport() && CommonAreas.catherbyBank().contains(ctx.players.local()) && !ctx.players.local().inMotion();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !CommonAreas.catherbyBank().contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = CommonAreas.catherbyBank().getRandomTile();
        ctx.movement.newTilePath(path).traverse();
    }
}
