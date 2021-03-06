package scripts.magic_zammywine;

import shared.templates.AbstractAction;
import shared.tools.RsLookup;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkTeleToBank extends AbstractAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    public static final Tile[] path = {new Tile(2961, 3381, 0), new Tile(2957, 3381, 0), new Tile(2953, 3378, 0), new Tile(2949, 3376, 0), new Tile(2946, 3373, 0), new Tile(2946, 3369, 0)};

    public WalkTeleToBank(ClientContext ctx) {
        super(ctx, "To Bank");
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && !ctx.bank.inViewport() && ctx.game.floor() == 0;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
