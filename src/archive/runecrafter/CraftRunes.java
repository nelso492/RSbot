package ngc.runecrafter;


import ngc._resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class CraftRunes extends BaseAction<ClientContext> {

    private int altarId;
    private int ruinsCoord;

    public CraftRunes(ClientContext ctx, int altarId, int ruinsCoord) {
        super(ctx, "Crafting");
        this.altarId = altarId;
        this.ruinsCoord = ruinsCoord;
    }

    @Override
    public boolean activate() {
        return ctx.players.local().tile().y() > ruinsCoord && ctx.objects.select().id(altarId).poll().valid() && ctx.inventory.isFull();
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject altar = ctx.objects.select().id(altarId).nearest().poll();

        if( altar.inViewport() ) {
            if( altar.interact("Craft-rune") ) {
                // Toggle Mouse Move
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.inventory.isFull();
                    }
                }, Random.nextInt(100, 200), 30);
            }
        } else {
            ctx.movement.step(altar);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return altar.inViewport() || !ctx.players.local().inMotion();
                }
            }, 250, 20);

        }
    }
}

