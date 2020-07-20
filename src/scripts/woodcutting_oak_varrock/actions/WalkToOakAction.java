package scripts.woodcutting_oak_varrock.actions;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import shared.constants.GameObjects;
import shared.templates.StructuredAction;
import shared.tools.AntibanTools;
import shared.tools.CommonAreas;

import java.util.concurrent.Callable;

/**
 * [Description]
 */
public class WalkToOakAction extends StructuredAction {
    public static final Tile[] path = {new Tile(3168, 3489, 0), new Tile(3172, 3489, 0), new Tile(3176, 3489, 0), new Tile(3180, 3489, 0), new Tile(3184, 3489, 0), new Tile(3188, 3489, 0), new Tile(3192, 3488, 0), new Tile(3192, 3484, 0), new Tile(3195, 3481, 0), new Tile(3194, 3477, 0), new Tile(3194, 3473, 0), new Tile(3194, 3469, 0), new Tile(3195, 3465, 0), new Tile(3195, 3461, 0)};
    private final Area oakArea = CommonAreas.getVarrockOakTrees();

    public WalkToOakAction(ClientContext ctx, String status) {
        super(ctx, status);
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && !ctx.objects.select().id(10820).nearest().poll().inViewport();
    }

    @Override
    public void execute() {
        path[path.length - 1] = oakArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();

        AntibanTools.sleepDelay(Random.nextInt(0, 2));
    }

    @Override
    public boolean isComplete() {
        return ctx.objects.select().id(10820).nearest().poll().inViewport();
    }
}
