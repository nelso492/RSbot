package scripts.magic_zammywine;

import resources.models.BaseAction;
import resources.enums.OBJECT_IDS;
import resources.tools.RsLookup;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkBankToChaosTower extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    private int towerLadderLowerId = lookup.getId(OBJECT_IDS.ChaosLadderLower_31580);

    public static final Tile[] path = {new Tile(2945, 3371, 0), new Tile(2949, 3374, 0), new Tile(2953, 3378, 0), new Tile(2958, 3380, 0), new Tile(2962, 3384, 0), new Tile(2963, 3389, 0), new Tile(2964, 3394, 0), new Tile(2964, 3399, 0), new Tile(2964, 3404, 0), new Tile(2962, 3409, 0), new Tile(2960, 3415, 0), new Tile(2957, 3419, 0), new Tile(2953, 3423, 0), new Tile(2950, 3427, 0), new Tile(2950, 3432, 0), new Tile(2948, 3437, 0), new Tile(2948, 3442, 0), new Tile(2948, 3447, 0), new Tile(2947, 3452, 0), new Tile(2946, 3457, 0), new Tile(2946, 3462, 0), new Tile(2944, 3467, 0), new Tile(2944, 3472, 0), new Tile(2944, 3477, 0), new Tile(2942, 3482, 0), new Tile(2942, 3487, 0), new Tile(2942, 3492, 0), new Tile(2939, 3496, 0), new Tile(2937, 3501, 0), new Tile(2941, 3504, 0), new Tile(2940, 3510, 0), new Tile(2942, 3515, 0), new Tile(2939, 3517, 0)};
    public WalkBankToChaosTower(ClientContext ctx) {
        super(ctx, "To Tower");
    }

    @Override
    public boolean activate() {
        boolean inChaosTower = ctx.objects.select().id(towerLadderLowerId).poll().inViewport();
        return ctx.inventory.select().count() == 2 && !inChaosTower && ctx.game.floor() == 0;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
