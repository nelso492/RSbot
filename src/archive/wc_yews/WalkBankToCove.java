package ngc.wc_yews;

import ngc._resources.constants.GameObjects;
import ngc._resources.models.BaseAction;
import ngc._resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

public class WalkBankToCove extends BaseAction<ClientContext> {

    public static final Tile[] path = {new Tile(2569, 2864, 0), new Tile(2566, 2861, 0), new Tile(2562, 2863, 0), new Tile(2558, 2863, 0), new Tile(2554, 2863, 0), new Tile(2550, 2861, 0), new Tile(2546, 2859, 0), new Tile(2542, 2857, 0), new Tile(2538, 2857, 0), new Tile(2534, 2857, 0), new Tile(2530, 2860, 0), new Tile(2526, 2861, 0), new Tile(2522, 2860, 0), new Tile(2518, 2861, 0), new Tile(2514, 2862, 0), new Tile(2510, 2862, 0), new Tile(2505, 2862, 0), new Tile(2501, 2865, 0), new Tile(2498, 2868, 0), new Tile(2494, 2867, 0), new Tile(2490, 2867, 0), new Tile(2486, 2866, 0), new Tile(2484, 2870, 0), new Tile(2482, 2874, 0), new Tile(2482, 2878, 0), new Tile(2482, 2882, 0), new Tile(2478, 2884, 0), new Tile(2475, 2888, 0)};
    private Area coveArea;
    private int yewId;

    public WalkBankToCove(ClientContext ctx) {
        super(ctx, "To Cove");
        this.yewId = GameObjects.YEW_TREE_1753;
        this.coveArea = CommonAreas.corsairCoveYews();
    }

    @Override
    public boolean activate() {

        return ctx.inventory.isEmpty() && !ctx.npcs.select().id(yewId).poll().inViewport() && !coveArea.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = coveArea.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
    }
}
