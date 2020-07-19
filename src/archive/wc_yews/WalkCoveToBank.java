package ngc.wc_yews;

import ngc._resources.models.BaseAction;
import ngc._resources.tools.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

public class WalkCoveToBank extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(2473, 2889, 0), new Tile(2476, 2886, 0), new Tile(2480, 2885, 0), new Tile(2482, 2881, 0), new Tile(2482, 2877, 0), new Tile(2482, 2873, 0), new Tile(2481, 2869, 0), new Tile(2485, 2869, 0), new Tile(2489, 2869, 0), new Tile(2493, 2869, 0), new Tile(2497, 2869, 0), new Tile(2500, 2866, 0), new Tile(2504, 2864, 0), new Tile(2508, 2864, 0), new Tile(2512, 2860, 0), new Tile(2516, 2859, 0), new Tile(2520, 2860, 0), new Tile(2524, 2859, 0), new Tile(2526, 2855, 0), new Tile(2530, 2855, 0), new Tile(2535, 2855, 0), new Tile(2539, 2855, 0), new Tile(2543, 2856, 0), new Tile(2547, 2857, 0), new Tile(2551, 2859, 0), new Tile(2555, 2860, 0), new Tile(2559, 2860, 0), new Tile(2563, 2858, 0), new Tile(2567, 2858, 0), new Tile(2570, 2861, 0)};
    private Area coveBank;

    public WalkCoveToBank(ClientContext ctx) {
        super(ctx, "To Bank");
        this.coveBank = CommonAreas.corsairBank();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !ctx.bank.inViewport() && !coveBank.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        path[path.length - 1] = coveBank.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
    }
}
