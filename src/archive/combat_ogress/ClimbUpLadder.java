package ngc.combat_ogress;

import resources.constants.GameObjects;
import resources.constants.Items;
import resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class ClimbUpLadder extends BaseAction<ClientContext> {

    public static final Tile[] path = {new Tile(2569, 2864, 0), new Tile(2566, 2861, 0), new Tile(2562, 2863, 0), new Tile(2558, 2863, 0), new Tile(2554, 2863, 0), new Tile(2550, 2861, 0), new Tile(2546, 2859, 0), new Tile(2542, 2857, 0), new Tile(2538, 2857, 0), new Tile(2534, 2857, 0), new Tile(2530, 2860, 0), new Tile(2526, 2861, 0), new Tile(2522, 2860, 0), new Tile(2518, 2861, 0), new Tile(2514, 2862, 0), new Tile(2510, 2862, 0), new Tile(2505, 2862, 0), new Tile(2501, 2865, 0), new Tile(2498, 2868, 0), new Tile(2494, 2867, 0), new Tile(2490, 2867, 0), new Tile(2486, 2866, 0), new Tile(2484, 2870, 0), new Tile(2482, 2874, 0), new Tile(2482, 2878, 0), new Tile(2482, 2882, 0), new Tile(2478, 2884, 0), new Tile(2475, 2888, 0)};
    private int ladderId;
    private int[] alchables;

    public ClimbUpLadder(ClientContext ctx, int[] alchables) {
        super(ctx, "Climb Up");
        this.ladderId = GameObjects.VINE_LADDER_31791;
        this.alchables = alchables;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() &&
                !(ctx.inventory.select().id(alchables).count() > 0 && ctx.inventory.select().id(Items.NATURE_RUNE_561).poll().stackSize() > 0 && ctx.inventory.select().id(Items.FIRE_RUNE_554).poll().stackSize() >= 5) &&
                ctx.objects.select().id(ladderId).peek().inViewport();
    }

    @Override
    public void execute() {
        // check for arrows first
        if( ctx.inventory.select().id(Items.IRON_ARROW_884).count() == 1 ) {
            ctx.inventory.select().id(Items.IRON_ARROW_884).poll().interact("Wield");
        } else {
            ctx.objects.poll().interact("Climb");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 0;
                }
            });
        }
    }
}
