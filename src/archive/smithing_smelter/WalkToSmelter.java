package scripts.smithing_smelter;


import shared.constants.GameObjects;
import shared.constants.Items;
import shared.templates.AbstractAction;
import shared.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToSmelter extends AbstractAction<ClientContext> {
    private int silver = Items.SILVER_ORE_442;

    private Area smelterArea = CommonAreas.edgevilleSmelter();
    public static final Tile[] path = {new Tile(3097, 3494, 0), new Tile(3101, 3496, 0), new Tile(3104, 3499, 0), new Tile(3108, 3499, 0)};

    public WalkToSmelter(ClientContext ctx) {
        super(ctx, "To Smelter");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(silver).count() == 28 && ctx.objects.select().id(GameObjects.FURNACE_EDGEVILLE).poll().tile().distanceTo(ctx.players.local()) > 5;
    }

    @Override
    public void execute() {
        path[path.length - 1] = smelterArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
