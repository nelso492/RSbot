package disabled.fishing_cage_harpoon.karamja;

import shared.templates.AbstractAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkShipToFishing extends AbstractAction<ClientContext> {
    public static final Tile[] path = {new Tile(2954, 3151, 0), new Tile(2954, 3147, 0), new Tile(2950, 3147, 0), new Tile(2946, 3147, 0), new Tile(2942, 3147, 0), new Tile(2938, 3145, 0), new Tile(2934, 3146, 0), new Tile(2930, 3148, 0), new Tile(2926, 3148, 0), new Tile(2922, 3150, 0), new Tile(2918, 3151, 0), new Tile(2916, 3155, 0), new Tile(2917, 3159, 0), new Tile(2917, 3163, 0), new Tile(2920, 3166, 0), new Tile(2920, 3170, 0), new Tile(2923, 3173, 0), new Tile(2925, 3177, 0)};
    private int gangplank;
    private Area fishingArea;
    private Area portSarimArea;

    public WalkShipToFishing(ClientContext ctx, int gangplank, Area fishingArea, Area portSarimArea) {
        super(ctx, "To Fishing");
        this.gangplank = gangplank;
        this.fishingArea = fishingArea;
        this.portSarimArea = portSarimArea;
    }

    @Override
    public boolean activate() {
        boolean notInFishingArea = !fishingArea.contains(ctx.players.local());
        boolean notInPortSarim = !portSarimArea.contains(ctx.players.local());
        boolean notOnShip = !ctx.objects.select().id(gangplank).poll().valid();

        return ctx.inventory.select().count() == 2 && notInFishingArea && notInPortSarim && notOnShip;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
