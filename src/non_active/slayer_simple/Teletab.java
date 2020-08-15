package scripts.slayer_simple;

import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

public class Teletab extends AbstractAction<ClientContext> {

    private int teletabId;
    private boolean suspendAfterTeleporting;

    public Teletab(ClientContext ctx, int teletabId, boolean suspendAfterTeleporting) {
        super(ctx, "Teleport");
        this.teletabId = teletabId;
        this.suspendAfterTeleporting = suspendAfterTeleporting;
    }

    @Override
    public boolean activate() {
        return ctx.inventory.isFull() && ctx.game.floor() == 1;
    }


    @Override
    public void execute() {
        Item tab = ctx.inventory.select().id(teletabId).first().poll();
        Tile playerTile = ctx.players.local().tile();

        if( tab.valid() ) {
            tab.interact("Break", tab.name());

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().tile().x() != playerTile.x();
                }
            }, 100, 30);

            if(suspendAfterTeleporting){
                ctx.controller.suspend();
            }
        }
    }

}
