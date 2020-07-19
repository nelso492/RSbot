package ngc.wc_oak_cutter;


import ngc._resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;


public class ChopOakAction extends BaseAction<ClientContext> {

    public ChopOakAction(ClientContext ctx) {
        super(ctx, "Chopping");
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;

        return playerNotAnimated;// && netInInventory;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject oakTree = ctx.objects.select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.name().equals("Oak");
            }
        }).nearest().poll();

        if( oakTree.inViewport() ) {
            oakTree.interact("Chop down");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            }, 250, 4);
        }
    }


}
