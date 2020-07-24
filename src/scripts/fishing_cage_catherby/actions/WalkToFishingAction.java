package scripts.fishing_cage_catherby.actions;

import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;

public class WalkToFishingAction extends StructuredAction {

    private static final Tile[] path = {new Tile(2808, 3437, 0), new Tile(2811, 3436, 0), new Tile(2822, 3438, 0), new Tile(2827, 3437, 0), new Tile(2828, 3436, 0), new Tile(2834, 3435, 0), new Tile(2839, 3434, 0), new Tile(2845, 3433, 0), new Tile(2851, 3428, 0)};

    public WalkToFishingAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean isComplete() {
        return ctx.npcs.select().id(1519).poll().inViewport();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 1 && !ctx.npcs.select().id(1519).poll().inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        AntibanTools.sleepDelay(Random.nextInt(1, 3));
    }
}
