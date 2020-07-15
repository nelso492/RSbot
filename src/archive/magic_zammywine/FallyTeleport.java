package ngc.magic_zammywine;


import ngc._resources.actions._template.BaseAction;
import ngc._resources.enums.ITEM_IDS;
import ngc._resources.functions.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Magic;

import java.awt.*;
import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class FallyTeleport extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    private int zammyWineId = lookup.getId(ITEM_IDS.WineOfZamorak_245);
    private int lawRuneId = lookup.getId(ITEM_IDS.LawRune_563);


    public FallyTeleport(ClientContext ctx) {
        super(ctx, "Teleport");
    }

    @Override
    public boolean activate() {
        int wineCount = ctx.inventory.select().id(zammyWineId).count();
        int lawCount = ctx.inventory.select().id(lawRuneId).poll().stackSize();

        return (wineCount == 26 || lawCount == 1) && ctx.game.floor() == 1;
    }

    @Override
    public void execute() {

        if(ctx.magic.casting(Magic.Spell.TELEKINETIC_GRAB)){
            Point p = new Point(Random.nextInt(605, 665), Random.nextInt(380, 420));
            ctx.input.click(p, true);
            sleep();
        }

        int ycoor = ctx.players.local().tile().y();
        ctx.magic.cast(Magic.Spell.FALADOR_TELEPORT);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().tile().y() != ycoor;
            }
        }, Random.nextInt(250, 300), 20);
    }
}
