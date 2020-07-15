package ngc.wc_willows;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.functions.CommonAreas;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkToBankAction extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3060, 3253, 0), new Tile(3056, 3253, 0), new Tile(3054, 3249, 0), new Tile(3051, 3246, 0), new Tile(3047, 3246, 0), new Tile(3043, 3246, 0), new Tile(3042, 3242, 0), new Tile(3042, 3238, 0), new Tile(3046, 3235, 0)};
    private Area sarimDeposit;
    private int minEnergy;

    public WalkToBankAction(ClientContext ctx, int minEnergy) {
        super(ctx, "To Bank");
        this.minEnergy = minEnergy;
        this.sarimDeposit = CommonAreas.getPortSarimDepositBox();
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 28 && !sarimDeposit.contains(ctx.players.local()) && ctx.movement.energyLevel() > minEnergy;
    }

    @Override
    public void execute() {
        path[path.length - 1] = sarimDeposit.getRandomTile();
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
