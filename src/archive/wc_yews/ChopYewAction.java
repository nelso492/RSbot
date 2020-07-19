package ngc.wc_yews;


import ngc._resources.constants.GameObjects;
import ngc._resources.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class ChopYewAction extends BaseAction<ClientContext> {
    private int yewTreeId = GameObjects.YEW_TREE_1753;

    public ChopYewAction(ClientContext ctx) {
        super(ctx, "Chopping");
    }

    @Override
    public boolean activate() {
        boolean invNotFull = ctx.inventory.select().count() < 28;
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean inTreeArea = !ctx.objects.select().id(yewTreeId).select(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject gameObject) {
                return gameObject.valid() && gameObject.inViewport();
            }
        }).isEmpty();

        return invNotFull && playerNotAnimated && inTreeArea;// && netInInventory;
    }

    @Override
    public void execute() {
        GameObject tree = ctx.objects.nearest().poll();

        if( tree.valid() && tree.inViewport() ) {
            if( tree.interact("Chop") ) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !tree.valid();
                    }
                }, Random.nextInt(500, 1500), 10);
            }
        }
    }


}
