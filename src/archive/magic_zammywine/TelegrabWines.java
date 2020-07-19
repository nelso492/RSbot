package scripts.magic_zammywine;


import resources.models.BaseAction;
import resources.enums.ITEM_IDS;
import resources.tools.GaussianProbability;
import resources.tools.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class TelegrabWines extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    private int zammyWineId = lookup.getId(ITEM_IDS.WineOfZamorak_245);
    private int lawRuneId = lookup.getId(ITEM_IDS.LawRune_563);
    private int wineCount = 0;

    public static final Tile[] path = {new Tile(3208, 3217, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};

    public TelegrabWines(ClientContext ctx) {
        super(ctx, "Telegrab");
    }

    @Override
    public boolean activate() {
       // wineCount = ctx.inventory.select().id(zammyWineId).count();
       // int lawCount = ctx.inventory.select().id(lawRuneId).poll().stackSize();
        boolean wineVisible = ctx.menu.items()[0].equals("Cast Telekinetic Grab -> Wine of zamorak");

        return (ctx.game.floor() == 1 && wineVisible);
    }

    @Override
    public void execute() {
        ctx.input.click(true);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.inventory.select().id(zammyWineId).count() != wineCount || ctx.players.local().animation() == -1;
            }
        }, Random.nextInt(450, 600), 20);

    }
}


