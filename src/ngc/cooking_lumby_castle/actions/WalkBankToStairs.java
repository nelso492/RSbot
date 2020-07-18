package ngc.cooking_lumby_castle.actions;


import ngc._resources.GameObjects;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import static org.powerbot.script.Condition.sleep;

public class WalkBankToStairs extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3208, 3217, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};
    private int fishId;

    public WalkBankToStairs(ClientContext ctx, int _fishId) {
        super(ctx, "To Downstairs");
        fishId = _fishId;
    }

    @Override
    public boolean activate() {
        boolean stairsVisible = ctx.objects.select().id(GameObjects.STAIRCASE_LUMBRIDGE_CASTLE_16673).select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.inViewport() && gameObject.tile().distanceTo(ctx.players.local()) < 4;
            }
        }).poll().valid();
        boolean allRawFish = ctx.inventory.select().id(fishId).count() > 0;
        return !stairsVisible && allRawFish && ctx.game.floor() == 2;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep(2000);

    }
}
