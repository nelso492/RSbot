package ngc.fishing_cage_harpoon.corsair;

import resources.models.BaseAction;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkBankToFishing extends BaseAction<ClientContext> {

    private Tile[] path;
    private int fishingSpotId;

    public WalkBankToFishing(ClientContext ctx, int fishingSpotId) {
        super(ctx, "Bank To Dock");
        this.path = new Tile[] {new Tile(2569, 2861, 0), new Tile(2565, 2861, 0), new Tile(2561, 2863, 0), new Tile(2557, 2863, 0), new Tile(2553, 2864, 0), new Tile(2550, 2861, 0), new Tile(2546, 2859, 0), new Tile(2542, 2857, 0), new Tile(2538, 2857, 0), new Tile(2534, 2856, 0), new Tile(2531, 2859, 0), new Tile(2527, 2860, 0), new Tile(2523, 2860, 0), new Tile(2519, 2860, 0), new Tile(2515, 2862, 0), new Tile(2511, 2862, 0), new Tile(2507, 2862, 0), new Tile(2503, 2863, 0), new Tile(2500, 2866, 0), new Tile(2497, 2870, 0), new Tile(2493, 2871, 0), new Tile(2489, 2871, 0), new Tile(2485, 2874, 0), new Tile(2482, 2877, 0), new Tile(2482, 2881, 0), new Tile(2479, 2884, 0), new Tile(2476, 2887, 0), new Tile(2472, 2889, 0), new Tile(2468, 2889, 0), new Tile(2464, 2891, 0), new Tile(2460, 2892, 0)};
        this.fishingSpotId = fishingSpotId;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 1 && !ctx.npcs.select().id(fishingSpotId).nearest().poll().inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
