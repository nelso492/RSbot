package ngc.fishing_cage_harpoon.karamja;

import ngc._resources.constants.Items;
import ngc._resources.models.BaseAction;
import org.powerbot.script.Area;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import static org.powerbot.script.Condition.sleep;

public class WalkFishingToShip extends BaseAction<ClientContext> {
    public static final Tile[] path = {new Tile(2925, 3179, 0), new Tile(2925, 3175, 0), new Tile(2925, 3171, 0), new Tile(2923, 3167, 0), new Tile(2923, 3163, 0), new Tile(2920, 3160, 0), new Tile(2917, 3157, 0), new Tile(2916, 3153, 0), new Tile(2920, 3153, 0), new Tile(2925, 3153, 0), new Tile(2929, 3152, 0), new Tile(2933, 3151, 0), new Tile(2936, 3148, 0), new Tile(2940, 3146, 0), new Tile(2944, 3146, 0), new Tile(2948, 3147, 0), new Tile(2952, 3147, 0)};
    private Area portSarim;
    private Area karmjaDock;
    private boolean dropTuna;

    public WalkFishingToShip(ClientContext ctx, Area portSarim, Area karmjaDock, boolean dropTuna) {
        super(ctx, "Fishing To Ship");
        this.portSarim = portSarim;
        this.karmjaDock = karmjaDock;
        this.dropTuna = dropTuna;
    }

    @Override
    public boolean activate() {
        boolean notInPortSarim = !portSarim.contains(ctx.players.local());
        boolean notInKaramjaDock = !karmjaDock.contains(ctx.players.local());
        boolean noTuna = !dropTuna || ctx.inventory.select().id(Items.RAW_TUNA_359).count() == 0;

        return ctx.inventory.select().count() == 28 && notInPortSarim && notInKaramjaDock && noTuna;
    }

    @Override
    public void execute() {
        ctx.movement.newTilePath(path).traverse();
        sleep();
    }
}
