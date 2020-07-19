package ngc.fishing_cage_harpoon.karamja;

import resources.models.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkShipToBank extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(3029, 3217, 0), new Tile(3027, 3221, 0), new Tile(3027, 3225, 0), new Tile(3027, 3229, 0), new Tile(3027, 3233, 0), new Tile(3030, 3236, 0), new Tile(3034, 3236, 0), new Tile(3038, 3236, 0), new Tile(3042, 3236, 0)};
    private Area depositBox;
    private Area portSarim;

    public WalkShipToBank(ClientContext ctx, Area depositBox, Area portSarim) {
        super(ctx, "To Bank");
        this.depositBox = depositBox;
        this.portSarim = portSarim;
    }

    @Override
    public boolean activate() {
        boolean inBankArea = depositBox.contains(ctx.players.local());
        boolean inPortSarim = portSarim.contains(ctx.players.local());
        return ctx.inventory.select().count() == 28 && !inBankArea && inPortSarim;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
