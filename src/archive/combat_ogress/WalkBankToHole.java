package scripts.combat_ogress;

import shared.constants.GameObjects;
import shared.templates.AbstractAction;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

public class WalkBankToHole extends AbstractAction<ClientContext> {

    private Tile[] path = {new Tile(2569, 2861, 0), new Tile(2565, 2861, 0), new Tile(2561, 2863, 0), new Tile(2557, 2863, 0), new Tile(2553, 2863, 0), new Tile(2549, 2860, 0), new Tile(2545, 2858, 0), new Tile(2541, 2857, 0), new Tile(2537, 2857, 0), new Tile(2533, 2857, 0), new Tile(2530, 2860, 0), new Tile(2526, 2860, 0)};
    private int holeId;

    public WalkBankToHole(ClientContext ctx) {
        super(ctx, "To Hole");
        this.holeId = GameObjects.HOLE_31791;
    }

    @Override
    public boolean activate() {
        return !ctx.inventory.isFull() && !ctx.objects.select().id(holeId).peek().inViewport() && ctx.game.floor() == 0;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
    }
}
