package scripts.misc_warriors_guild_animator;

import shared.templates.AbstractAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBank extends AbstractAction<ClientContext> {

    private int[] doorIds;
    private Tile[] pathToBank;

    private int helmId, bodyId, legsId, foodId;
    private Area animatorArea;

    public WalkToBank(ClientContext ctx, int helmId, int bodyId, int legsId, int foodId) {
        super(ctx, "Animator to Bank");
        this.doorIds = new int[] {34309, 34306};
        this.helmId = helmId;
        this.bodyId = bodyId;
        this.legsId = legsId;
        this.foodId = foodId;
        this.animatorArea = new Area(new Tile(2849, 3454), new Tile(2861, 3545));
        this.pathToBank = new Tile[] {new Tile(2854, 3546, 0), new Tile(2850, 3546, 0), new Tile(2846, 3544, 0)};
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(helmId).count() == 1
                && ctx.inventory.select().id(bodyId).count() == 1
                && ctx.inventory.select().id(legsId).count() == 1
                && ctx.inventory.select().id(foodId).count() == 0
                && !animatorArea.contains(ctx.players.local())
                && !ctx.bank.inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(pathToBank).traverse();
        sleep();
    }
}
