package scripts.combat_ogress;

import resources.constants.GameObjects;
import resources.models.BaseAction;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

public class WalkHoleToBank extends BaseAction<ClientContext> {

    Tile[] path = {new Tile(2523, 2860, 0), new Tile(2527, 2860, 0), new Tile(2531, 2860, 0), new Tile(2535, 2859, 0), new Tile(2539, 2857, 0), new Tile(2543, 2857, 0), new Tile(2547, 2858, 0), new Tile(2550, 2861, 0), new Tile(2554, 2863, 0), new Tile(2558, 2863, 0), new Tile(2562, 2863, 0), new Tile(2566, 2863, 0), new Tile(2570, 2861, 0)};
    private int depositBoxId;

    public WalkHoleToBank(ClientContext ctx) {
        super(ctx, "To Bank");
        this.depositBoxId = GameObjects.DEPOSIT_BOX_31726;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !ctx.objects.select().id(depositBoxId).peek().inViewport();
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
    }
}
