package scripts.cooking_lumby_castle.actions;


import shared.constants.GameObjects;
import shared.models.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.util.concurrent.Callable;

public class CookFood extends BaseAction<ClientContext> {

    private int rangeId;
    private int rawFoodId;


    public CookFood(ClientContext ctx, int _rawFoodId) {
        super(ctx, "Food");
        rawFoodId = _rawFoodId;
        this.rangeId = GameObjects.COOKING_RANGE_LUMBRIDGE;
    }

    @Override
    public boolean activate() {
        boolean playerNotAnimated = ctx.players.local().animation() == -1;
        boolean playerNotInteracting = !ctx.players.local().interacting().valid();
        boolean hasRawFood = ctx.inventory.select().id(rawFoodId).count() > 0;

        return hasRawFood && playerNotAnimated && playerNotInteracting && ctx.game.floor() == 0;// && netInInventory;
    }

    @Override
    public void execute() {
        // Travel to fishing location
        GameObject cookingRange = ctx.objects.select().id(rangeId).nearest().poll();

        if (cookingRange.inViewport()) {
            if (cookingRange.interact("Cook")) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion() && ctx.widgets.component(270, 13).valid();
                    }
                }, Random.nextInt(500, 800), 10);
                //ctx.widgets.component(270, 13).click();
                ctx.input.sendln(" ");
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.select().id(rawFoodId).count() == 0;
                    }
                }, Random.nextInt(1000, 2000), 4);
            } else {
                ctx.movement.step(cookingRange);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !ctx.players.local().inMotion();
                    }
                }, Random.nextInt(500, 777), 4);
            }
        }else {
            ctx.movement.step(cookingRange);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            }, Random.nextInt(500, 777), 4);
        }
    }
}
