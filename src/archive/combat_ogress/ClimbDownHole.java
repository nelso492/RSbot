package scripts.combat_ogress;

import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class ClimbDownHole extends AbstractAction<ClientContext> {

    public static final Tile[] path = {new Tile(2569, 2864, 0), new Tile(2566, 2861, 0), new Tile(2562, 2863, 0), new Tile(2558, 2863, 0), new Tile(2554, 2863, 0), new Tile(2550, 2861, 0), new Tile(2546, 2859, 0), new Tile(2542, 2857, 0), new Tile(2538, 2857, 0), new Tile(2534, 2857, 0), new Tile(2530, 2860, 0), new Tile(2526, 2861, 0), new Tile(2522, 2860, 0), new Tile(2518, 2861, 0), new Tile(2514, 2862, 0), new Tile(2510, 2862, 0), new Tile(2505, 2862, 0), new Tile(2501, 2865, 0), new Tile(2498, 2868, 0), new Tile(2494, 2867, 0), new Tile(2490, 2867, 0), new Tile(2486, 2866, 0), new Tile(2484, 2870, 0), new Tile(2482, 2874, 0), new Tile(2482, 2878, 0), new Tile(2482, 2882, 0), new Tile(2478, 2884, 0), new Tile(2475, 2888, 0)};
    private int holeId;

    public ClimbDownHole(ClientContext ctx) {
        super(ctx, "Climb Down");
        this.holeId = GameObjects.HOLE_31791;
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && ctx.objects.select().id(holeId).peek().inViewport();
    }

    @Override
    public void execute() {
        ctx.objects.poll().interact("Enter", "Hole");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.game.floor() == 1;
            }
        });

        ctx.movement.step(new Tile(2011, 9002, 1));
    }
}
