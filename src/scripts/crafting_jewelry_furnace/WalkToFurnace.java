package scripts.crafting_jewelry_furnace;


import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class WalkToFurnace extends AbstractAction<ClientContext> {


    private int resource;
    private int furnaceId;
    private Area smelterArea = CommonAreas.edgevilleSmelter();
    public static final Tile[] path = {new Tile(3097, 3494, 0), new Tile(3101, 3496, 0), new Tile(3104, 3499, 0), new Tile(3108, 3499, 0)};

    public WalkToFurnace(ClientContext ctx, int resourceId) {
        super(ctx, "To Furnace");
        this.resource = resourceId;
        this.furnaceId = GameObjects.FURNACE_EDGEVILLE;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(resource).count() == 27 && ctx.objects.select().id(furnaceId).poll().tile().distanceTo(ctx.players.local()) > 5;
    }

    @Override
    public void execute() {
        path[path.length - 1] = smelterArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.objects.select().id(furnaceId).nearest().poll().inViewport();
            }
        }, 100, 50);
    }
}
