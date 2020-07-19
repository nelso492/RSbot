package ngc.magic_zammywine;


import ngc._resources.models.BaseAction;
import ngc._resources.enums.ITEM_IDS;
import ngc._resources.enums.OBJECT_IDS;
import ngc._resources.tools.RsLookup;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class ClimbLadder extends BaseAction<ClientContext> {
    private RsLookup lookup = new RsLookup();

    private int zammyWineId =  lookup.getId(ITEM_IDS.WineOfZamorak_245);
    private int ladderLowerId = lookup.getId(OBJECT_IDS.ChaosLadderLower_31580);

    public static final Tile[] path = {new Tile(3208, 3217, 2), new Tile(3206, 3213, 2), new Tile(3205, 3209, 2)};

    public ClimbLadder(ClientContext ctx) {
        super(ctx, "Up Ladder");
    }

    @Override
    public boolean activate() {
        GameObject lowerLadder = ctx.objects.select().id(ladderLowerId).nearest().poll();
        int wineCount = ctx.inventory.select().id(zammyWineId).count();

        return (lowerLadder.inViewport() && wineCount == 0 && ctx.game.floor() == 0);
    }

    @Override
    public void execute() {
        GameObject ladder = ctx.objects.select().id(ladderLowerId).nearest().poll();
        if(ladder.inViewport()) {
            ladder.interact("Climb", ladder.name());
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.floor() == 1;
                }
            }, Random.nextInt(250, 500), 5);

            ctx.input.scroll(false);
        }else{
            ctx.camera.turnTo(ladder);
        }

    }
}
