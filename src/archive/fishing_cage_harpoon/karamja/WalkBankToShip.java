package scripts.fishing_cage_harpoon.karamja;

import shared.models.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkBankToShip extends BaseAction<ClientContext> {

    private static final Tile[] path = {new Tile(3045, 3235, 0), new Tile(3041, 3236, 0), new Tile(3037, 3236, 0), new Tile(3033, 3236, 0), new Tile(3029, 3236, 0), new Tile(3028, 3232, 0), new Tile(3028, 3228, 0), new Tile(3028, 3224, 0), new Tile(3027, 3220, 0)};
    private Area portSarim;
    private int SEAMAN_ID;

    public WalkBankToShip(ClientContext ctx, Area portSarim, int seamanId) {
        super(ctx, "Bank To Ship");
        this.portSarim = portSarim;
        this.SEAMAN_ID = seamanId;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().count() == 2 && !ctx.npcs.select().id(SEAMAN_ID).poll().inViewport() && portSarim.contains(ctx.players.local());
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
