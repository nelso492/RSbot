package scripts.fishing_net_draynor.actions;

import org.powerbot.script.Area;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;
import shared.tools.CommonAreas;

public class WalkToFishingAction extends StructuredAction {

    private static final Tile[] path = {new Tile(3092, 3245, 0), new Tile(3088, 3247, 0), new Tile(3087, 3243, 0), new Tile(3087, 3239, 0), new Tile(3087, 3235, 0), new Tile(3086, 3231, 0), new Tile(3087, 3227, 0)};

    public WalkToFishingAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean isComplete() {
        return ctx.npcs.select().id(1525).poll().inViewport();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 1 && !ctx.npcs.select().id(1525).poll().inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        AntibanTools.sleepDelay(Random.nextInt(1, 3));
    }
}
