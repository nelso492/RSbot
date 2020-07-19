package ngc.misc_barb_village_looter;


import resources.models.BaseAction;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBankAction extends BaseAction<ClientContext> {
    private static final Tile[] bankPath = {new Tile(3103, 3430, 0), new Tile(3100, 3434, 0), new Tile(3096, 3437, 0), new Tile(3094, 3441, 0), new Tile(3091, 3445, 0), new Tile(3091, 3449, 0), new Tile(3093, 3453, 0), new Tile(3095, 3457, 0), new Tile(3092, 3460, 0), new Tile(3088, 3462, 0), new Tile(3091, 3465, 0), new Tile(3095, 3465, 0), new Tile(3098, 3468, 0), new Tile(3098, 3472, 0), new Tile(3098, 3476, 0), new Tile(3098, 3480, 0), new Tile(3095, 3483, 0), new Tile(3091, 3486, 0), new Tile(3090, 3490, 0), new Tile(3094, 3490, 0)};

    public WalkToBankAction(ClientContext ctx) {
        super(ctx, "Walk to Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 28 && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(bankPath).traverse();
        sleep();
    }
}
