package scripts.woodcutting_oak_draynor.actions;

import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;

public class DraynorWalkToOakAction extends StructuredAction {
    public static final Tile[] path = {new Tile(3093, 3247, 0),new Tile(3099, 3247, 0),new Tile(3101, 3244, 0),};

    public DraynorWalkToOakAction(ClientContext ctx, String status) {
        super(ctx, status);
    }
    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && !ctx.objects.select().id(10820).nearest().poll().inViewport();
    }

    @Override
    public void execute() {
        path[path.length - 1] = new Tile(AntibanTools.getRandomInRange(3099, 3102), AntibanTools.getRandomInRange(3241, 3246));
        ctx.movement.newTilePath(path).traverse();

        AntibanTools.sleepDelay(AntibanTools.getRandomInRange(0, 2));
    }

    @Override
    public boolean isComplete() {
        return ctx.objects.select().id(10820).nearest().poll().inViewport();
    }
}
