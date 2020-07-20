package scripts.wc_willows;


import shared.templates.AbstractAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class ChopWillowAction extends AbstractAction<ClientContext> {
    private int willowId;

    public ChopWillowAction(ClientContext ctx) {
        super(ctx, "Chopping");
        this.willowId = 10819;
    }

    @Override
    public boolean activate() {
        boolean invNotFull = ctx.inventory.select().count() < 28;
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean inTreeArea = ctx.objects.select().id(willowId).select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.inViewport();
            }
        }).poll().valid();

        return invNotFull && playerNotAnimated && inTreeArea;// && netInInventory;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject tree = ctx.objects.select().id(willowId).nearest().poll();

        if( tree.valid() && tree.inViewport() ) {
            tree.interact("Chop down");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !tree.valid() || ctx.inventory.isFull();
                }
            }, 250, 100);
        }
    }


}
