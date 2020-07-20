package scripts.fishing_cage_harpoon.corsair;

import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkFishingToBank extends AbstractAction<ClientContext> {

    private Tile[] path;

    public WalkFishingToBank(ClientContext ctx) {
        super(ctx, "Dock to Bank");
        this.path = new Tile[] {new Tile(2456, 2891, 0), new Tile(2460, 2891, 0), new Tile(2464, 2891, 0), new Tile(2468, 2891, 0), new Tile(2470, 2887, 0), new Tile(2472, 2883, 0), new Tile(2476, 2883, 0), new Tile(2480, 2884, 0), new Tile(2482, 2880, 0), new Tile(2482, 2876, 0), new Tile(2483, 2872, 0), new Tile(2487, 2872, 0), new Tile(2491, 2872, 0), new Tile(2494, 2869, 0), new Tile(2498, 2868, 0), new Tile(2502, 2865, 0), new Tile(2503, 2861, 0), new Tile(2506, 2858, 0), new Tile(2510, 2857, 0), new Tile(2514, 2856, 0), new Tile(2518, 2854, 0), new Tile(2521, 2851, 0), new Tile(2525, 2851, 0), new Tile(2529, 2851, 0), new Tile(2533, 2849, 0), new Tile(2537, 2849, 0), new Tile(2540, 2852, 0), new Tile(2544, 2854, 0), new Tile(2547, 2857, 0), new Tile(2550, 2860, 0), new Tile(2553, 2863, 0), new Tile(2557, 2863, 0), new Tile(2561, 2863, 0), new Tile(2565, 2861, 0), new Tile(2569, 2861, 0)};
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !ctx.npcs.select().id(GameObjects.DEPOSIT_BOX_31726).nearest().poll().inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
